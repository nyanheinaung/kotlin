/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DiagnosticWithParameters2<E extends PsiElement, A, B> extends AbstractDiagnostic<E> {
    private final A a;
    private final B b;

    public DiagnosticWithParameters2(
            @NotNull E psiElement,
            @NotNull A a,
            @NotNull B b,
            @NotNull DiagnosticFactory2<E, A, B> factory,
            @NotNull Severity severity
    ) {
        super(psiElement, factory, severity);
        this.a = a;
        this.b = b;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public DiagnosticFactory2<E, A, B> getFactory() {
        return (DiagnosticFactory2<E, A, B>) super.getFactory();
    }

    @NotNull
    public A getA() {
        return a;
    }

    @NotNull
    public B getB() {
        return b;
    }

    @Override
    public String toString() {
        return getFactory() + "(a = " + a + ", b = " + b + ")";
    }
}
