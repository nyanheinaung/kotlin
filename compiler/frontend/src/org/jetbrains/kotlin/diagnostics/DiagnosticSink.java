/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;

import java.util.List;

public interface DiagnosticSink {
    DiagnosticSink DO_NOTHING = new DiagnosticSink() {
        @Override
        public void report(@NotNull Diagnostic diagnostic) {
        }

        @Override
        public boolean wantsDiagnostics() {
            return false;
        }
    };

    DiagnosticSink THROW_EXCEPTION = new DiagnosticSink() {
        @Override
        public void report(@NotNull Diagnostic diagnostic) {
            if (diagnostic.getSeverity() == Severity.ERROR) {
                PsiFile psiFile = diagnostic.getPsiFile();
                List<TextRange> textRanges = diagnostic.getTextRanges();
                String diagnosticText = DefaultErrorMessages.render(diagnostic);
                throw new IllegalStateException(diagnostic.getFactory().getName() + ": " + diagnosticText + " " + PsiDiagnosticUtils
                        .atLocation(psiFile, textRanges.get(0)));
            }
        }

        @Override
        public boolean wantsDiagnostics() {
            return true;
        }
    };

    void report(@NotNull Diagnostic diagnostic);
    boolean wantsDiagnostics();
}
