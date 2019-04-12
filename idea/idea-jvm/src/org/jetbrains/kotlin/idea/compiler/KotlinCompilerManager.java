/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.compiler;

import com.intellij.diagnostic.PluginException;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.js.JavaScript;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Set;

import static org.jetbrains.kotlin.config.CompilerRunnerConstants.INTERNAL_ERROR_PREFIX;
import static org.jetbrains.kotlin.config.CompilerRunnerConstants.KOTLIN_COMPILER_NAME;

public class KotlinCompilerManager extends AbstractProjectComponent {
    private static final Logger LOG = Logger.getInstance(KotlinCompilerManager.class);

    // Comes from external make
    private static final String PREFIX_WITH_COMPILER_NAME = KOTLIN_COMPILER_NAME + ": " + INTERNAL_ERROR_PREFIX;
    private static final Set<String> FILE_EXTS_WHICH_NEEDS_REFRESH = ContainerUtil.immutableSet(JavaScript.DOT_EXTENSION, ".map");

    public KotlinCompilerManager(Project project, CompilerManager manager) {
        super(project);
        manager.addCompilableFileType(KotlinFileType.INSTANCE);
        manager.addCompilationStatusListener(new CompilationStatusListener() {
            @Override
            public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                for (CompilerMessage error : compileContext.getMessages(CompilerMessageCategory.ERROR)) {
                    String message = error.getMessage();
                    if (message.startsWith(INTERNAL_ERROR_PREFIX) || message.startsWith(PREFIX_WITH_COMPILER_NAME)) {
                        LOG.error(new KotlinCompilerException(message));
                    }
                }
            }

            @Override
            public void fileGenerated(String outputRoot, String relativePath) {
                if (ApplicationManager.getApplication().isUnitTestMode()) return;

                String ext = FileUtilRt.getExtension(relativePath).toLowerCase();

                if (FILE_EXTS_WHICH_NEEDS_REFRESH.contains(ext)) {
                    String outFile = outputRoot + "/" + relativePath;
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(outFile);
                    assert virtualFile != null : "Virtual file not found for generated file path: " + outFile;
                    virtualFile.refresh(/*async =*/ false, /*recursive =*/ false);
                }
            }
        }, project);
    }

    // Extending PluginException ensures that Exception Analyzer recognizes this as a Kotlin exception
    private static class KotlinCompilerException extends PluginException {
        private final String text;

        public KotlinCompilerException(String text) {
            super("", PluginManagerCore.getPluginByClassName(KotlinCompilerManager.class.getName()));
            this.text = text;
        }

        @Override
        public void printStackTrace(PrintWriter s) {
            s.print(text);
        }

        @Override
        public void printStackTrace(@NotNull PrintStream s) {
            s.print(text);
        }

        @NotNull
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            LOG.error("Somebody called getStackTrace() on KotlinCompilerException");
            // Return some stack trace that originates in Kotlin
            return new UnsupportedOperationException().getStackTrace();
        }

        @Override
        public String getMessage() {
            return "<Exception from standalone Kotlin compiler>";
        }
    }
}
