/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DiagnosticWithParameters1<E extends PsiElement, A> extends AbstractDiagnostic<E> {
    private final A a;

    public DiagnosticWithParameters1(
            @NotNull E psiElement,
            @NotNull A a,
            @NotNull DiagnosticFactory1<E, A> factory,
            @NotNull Severity severity
    ) {
        super(psiElement, factory, severity);
        this.a = a;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public DiagnosticFactory1<E, A> getFactory() {
        return (DiagnosticFactory1<E, A>) super.getFactory();
    }

    @NotNull
    public A getA() {
        return a;
    }

    @Override
    public String toString() {
        return getFactory() + "(a = " + a + ")";
    }
}
