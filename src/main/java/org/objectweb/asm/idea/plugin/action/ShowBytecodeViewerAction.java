/*
 *
 *  Copyright 2011 Cédric Champeau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.objectweb.asm.idea.plugin.action;

import com.intellij.ide.util.JavaAnonymousClassesHelper;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.idea.plugin.AsmParser;
import org.objectweb.asm.idea.plugin.common.Constants;
import org.objectweb.asm.idea.plugin.config.ASMPluginComponent;
import org.objectweb.asm.idea.plugin.config.ApplicationConfig;
import org.objectweb.asm.idea.plugin.view.BytecodeOutline;
import reloc.org.objectweb.asm.ClassReader;
import reloc.org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Semaphore;

/**
 * Given a java file (or any file which generates classes), tries to locate a .class file. If the compilation state is
 * not up to date, performs an automatic compilation of the class. If the .class file can be located, generates bytecode
 * instructions for the class and ASMified code, and displays them into a tool window.
 *
 * @author Cédric Champeau
 * @author Kamiel Ahmadpour
 */
public class ShowBytecodeViewerAction extends AnAction {

    @Override
    public void update(final AnActionEvent e) {
        final VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        final Presentation presentation = e.getPresentation();
        if (project == null || virtualFile == null) {
            presentation.setEnabled(false);
            return;
        }
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        presentation.setEnabled(psiFile instanceof PsiClassOwner);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project == null || virtualFile == null) return;
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile instanceof PsiClassOwner) {
            if ("class".equals(virtualFile.getExtension())) {
                updateToolWindowContents(project, virtualFile);
            } else if (!virtualFile.isInLocalFileSystem() && !virtualFile.isWritable()) {
                // probably a source file in a library
                final PsiClass[] psiClasses = ((PsiClassOwner) psiFile).getClasses();
                if (psiClasses.length > 0) {
                    updateToolWindowContents(project, psiClasses[0].getOriginalElement().getContainingFile().getVirtualFile());
                }
            } else {
                Module module = ModuleUtil.findModuleForPsiElement(psiFile);
                if (module == null) {
                    return;
                }
                Application application = ApplicationManager.getApplication();
                application.runWriteAction(() -> FileDocumentManager.getInstance().saveAllDocuments());
                application.executeOnPooledThread(() -> {
                    final CompilerModuleExtension cme = CompilerModuleExtension.getInstance(module);
                    if (cme == null) {
                        return;
                    }
                    final CompilerManager compilerManager = CompilerManager.getInstance(project);
                    final VirtualFile[] files = {virtualFile};
                    final CompileScope compileScope = compilerManager.createFilesCompileScope(files);
                    final VirtualFile[] result = {null};
                    final VirtualFile[] outputDirectories = cme.getOutputRoots(true);
                    final Semaphore semaphore = new Semaphore(1);
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e1) {
                        result[0] = null;
                    }
                    if (compilerManager.isUpToDate(compileScope)) {
                        application.invokeLater(() -> {
                            result[0] = findClassFile(outputDirectories, psiFile);
                            semaphore.release();
                        });
                    } else {
                        application.invokeLater(() -> compilerManager.compile(files, (aborted, errors, warnings, compileContext) -> {
                            if (errors == 0) {
                                result[0] = findClassFile(outputDirectories, psiFile);
                            }
                            semaphore.release();
                        }));
                        try {
                            semaphore.acquire();
                        } catch (InterruptedException e1) {
                            result[0] = null;
                        }
                    }
                    application.invokeLater(() -> updateToolWindowContents(project, result[0]));
                });
            }
        }
    }

    private VirtualFile findClassFile(final VirtualFile[] outputDirectories, final PsiFile psiFile) {
        return ApplicationManager.getApplication().runReadAction(new Computable<VirtualFile>() {

            @Override
            public VirtualFile compute() {
                if (outputDirectories != null && psiFile instanceof PsiClassOwner) {
                    FileEditor editor = FileEditorManager.getInstance(psiFile.getProject()).getSelectedEditor(psiFile.getVirtualFile());
                    int caretOffset = (editor == null) ? -1 : ((PsiAwareTextEditorImpl) editor).getEditor().getCaretModel().getOffset();
                    if (caretOffset >= 0) {
                        PsiElement psiElement = psiFile.findElementAt(caretOffset);
                        PsiClass classAtCaret = findClassAtCaret(psiElement);
                        if (classAtCaret != null) {
                            return getClassFile(classAtCaret);
                        }
                    }
                    PsiClassOwner psiJavaFile = (PsiClassOwner) psiFile;
                    for (PsiClass psiClass : psiJavaFile.getClasses()) {
                        final VirtualFile file = getClassFile(psiClass);
                        if (file != null) {
                            return file;
                        }
                    }
                }
                return null;
            }

            private VirtualFile getClassFile(PsiClass psiClass) {
                String className = psiClass.getQualifiedName();
                if (className == null) {
                    if (psiClass instanceof PsiAnonymousClass) {
                        PsiClass parentClass = PsiTreeUtil.getParentOfType(psiClass, PsiClass.class);
                        if (parentClass != null) {
                            className = parentClass.getQualifiedName() + JavaAnonymousClassesHelper.getName((PsiAnonymousClass) psiClass);
                        }
                    } else {
                        PsiClass parentOfType = PsiTreeUtil.getParentOfType(psiClass, PsiClass.class);
                        if (parentOfType != null) {
                            className = parentOfType.getQualifiedName();
                        }
                    }
                }
                if (className != null) {
                    StringBuilder sb = new StringBuilder(className);
                    while (psiClass.getContainingClass() != null) {
                        sb.setCharAt(sb.lastIndexOf("."), '$');
                        psiClass = psiClass.getContainingClass();
                    }
                    String classFileName = sb.toString().replace('.', '/') + ".class";
                    for (VirtualFile outputDirectory : outputDirectories) {
                        final VirtualFile file = outputDirectory.findFileByRelativePath(classFileName);
                        if (file != null && file.exists()) {
                            return file;
                        }
                    }
                }
                return null;
            }

            private PsiClass findClassAtCaret(PsiElement psiElement) {
                while (psiElement != null) {
                    if (psiElement instanceof PsiClass) {
                        return (PsiClass) psiElement;
                    }
                    psiElement = psiElement.getParent();
                    findClassAtCaret(psiElement);
                }
                return null;
            }
        });
    }

    /**
     * Reads the .class file, processes it through the ASM TraceVisitor and ASMifier to update the contents of the two
     * tabs of the tool window.
     *
     * @param project the project instance
     * @param file    the class file
     */
    private void updateToolWindowContents(final Project project, final VirtualFile file) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            BytecodeOutline bytecodeOutline = BytecodeOutline.getInstance(project);
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

            if (file == null) {
                bytecodeOutline.setCode(file, Constants.NO_CLASS_FOUND);
                toolWindowManager.getToolWindow(Constants.PLUGIN_WINDOW_NAME).activate(null);
                return;
            }

            StringWriter stringWriter = new StringWriter();
            ClassReader reader = null;
            try {
                file.refresh(false, false);
                reader = new ClassReader(file.contentsToByteArray());
            } catch (IOException e) {
                return;
            }
            int flags = 0;
            ApplicationConfig applicationConfig = ASMPluginComponent.getApplicationConfig();
            if (applicationConfig.isSkipDebug()) flags = flags | ClassReader.SKIP_DEBUG;
            if (applicationConfig.isSkipFrames()) flags = flags | ClassReader.SKIP_FRAMES;
            if (applicationConfig.isExpandFrames()) flags = flags | ClassReader.EXPAND_FRAMES;
            if (applicationConfig.isSkipCode()) flags = flags | ClassReader.SKIP_CODE;

            reader.accept(new TraceClassVisitor(new PrintWriter(stringWriter)), flags);
            AsmParser asmParser = new AsmParser();
            String lines = asmParser.toAsm(stringWriter.toString());
            bytecodeOutline.setCode(file, lines);

            toolWindowManager.getToolWindow(Constants.PLUGIN_WINDOW_NAME).activate(null);
        });
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
