/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDiagnostic<E extends PsiElement> implements ParametrizedDiagnostic<E> {
    private final E psiElement;
    private final DiagnosticFactoryWithPsiElement<E, ?> factory;
    private final Severity severity;

    public AbstractDiagnostic(@NotNull E psiElement,
            @NotNull DiagnosticFactoryWithPsiElement<E, ?> factory,
            @NotNull Severity severity) {
        this.psiElement = psiElement;
        this.factory = factory;
        this.severity = severity;
    }

    @NotNull
    @Override
    public DiagnosticFactoryWithPsiElement<E, ?> getFactory() {
        return factory;
    }

    @NotNull
    @Override
    public PsiFile getPsiFile() {
        return psiElement.getContainingFile();
    }

    @NotNull
    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    @NotNull
    public E getPsiElement() {
        return psiElement;
    }

    @Override
    @NotNull
    public List<TextRange> getTextRanges() {
        return getFactory().getTextRanges(this);
    }

    @Override
    public boolean isValid() {
        if (!getFactory().isValid(this)) return false;
        return true;
    }
}
