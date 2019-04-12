/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.List;

public abstract class DiagnosticFactoryWithPsiElement<E extends PsiElement, D extends Diagnostic> extends DiagnosticFactory<D> {
    protected final PositioningStrategy<? super E> positioningStrategy;

    public DiagnosticFactoryWithPsiElement(Severity severity, PositioningStrategy<? super E> positioningStrategy) {
        super(severity);
        this.positioningStrategy = positioningStrategy;
    }

    protected List<TextRange> getTextRanges(ParametrizedDiagnostic<E> diagnostic) {
        return positioningStrategy.markDiagnostic(diagnostic);
    }

    protected boolean isValid(ParametrizedDiagnostic<E> diagnostic) {
        return positioningStrategy.isValid(diagnostic.getPsiElement());
    }
}
