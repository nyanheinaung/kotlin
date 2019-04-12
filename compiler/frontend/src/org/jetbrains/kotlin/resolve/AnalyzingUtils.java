/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.diagnostics.Diagnostic;
import org.jetbrains.kotlin.diagnostics.DiagnosticSink;
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid;
import org.jetbrains.kotlin.psi.debugText.DebugTextUtilKt;
import org.jetbrains.kotlin.resolve.diagnostics.Diagnostics;

import java.util.ArrayList;
import java.util.List;

public class AnalyzingUtils {
    private static final boolean WRITE_DEBUG_TRACE_NAMES = false;

    public abstract static class PsiErrorElementVisitor extends KtTreeVisitorVoid {
        @Override
        public abstract void visitErrorElement(@NotNull PsiErrorElement element);
    }

    public static void checkForSyntacticErrors(@NotNull PsiElement root) {
        root.acceptChildren(new PsiErrorElementVisitor() {
            @Override
            public void visitErrorElement(@NotNull PsiErrorElement element) {
                throw new IllegalArgumentException(element.getErrorDescription() + "; looking at " +
                                                   element.getNode().getElementType() + " '" +
                                                   element.getText() + PsiDiagnosticUtils.atLocation(element));
            }
        });
    }
    
    public static List<PsiErrorElement> getSyntaxErrorRanges(@NotNull PsiElement root) {
        List<PsiErrorElement> r = new ArrayList<>();
        root.acceptChildren(new PsiErrorElementVisitor() {
            @Override
            public void visitErrorElement(@NotNull PsiErrorElement element) {
                r.add(element);
            }
        });
        return r;
    }

    public static void throwExceptionOnErrors(BindingContext bindingContext) {
        throwExceptionOnErrors(bindingContext.getDiagnostics());
    }

    public static void throwExceptionOnErrors(Diagnostics diagnostics) {
        for (Diagnostic diagnostic : diagnostics) {
            DiagnosticSink.THROW_EXCEPTION.report(diagnostic);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------
    public static String formDebugNameForBindingTrace(@NotNull String debugName, @Nullable Object resolutionSubjectForMessage) {
        if (WRITE_DEBUG_TRACE_NAMES) {
            StringBuilder debugInfo = new StringBuilder(debugName);
            if (resolutionSubjectForMessage instanceof KtElement) {
                KtElement element = (KtElement) resolutionSubjectForMessage;
                debugInfo.append(" ").append(DebugTextUtilKt.getDebugText(element));
                //debugInfo.append(" in ").append(element.getContainingFile().getName());
                debugInfo.append(" in ").append(element.getContainingKtFile().getName()).append(" ").append(element.getTextOffset());
            }
            else if (resolutionSubjectForMessage != null) {
                debugInfo.append(" ").append(resolutionSubjectForMessage);
            }

            return debugInfo.toString();
        }

        return "";
    }
}
