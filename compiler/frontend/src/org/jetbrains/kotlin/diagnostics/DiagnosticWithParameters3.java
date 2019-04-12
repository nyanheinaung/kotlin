/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DiagnosticWithParameters3<E extends PsiElement, A, B, C> extends AbstractDiagnostic<E> {
    private final A a;
    private final B b;
    private final C c;

    public DiagnosticWithParameters3(
            @NotNull E psiElement,
            @NotNull A a,
            @NotNull B b,
            @NotNull C c,
            @NotNull DiagnosticFactory3<E, A, B, C> factory,
            @NotNull Severity severity
    ) {
        super(psiElement, factory, severity);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public DiagnosticFactory3<E, A, B, C> getFactory() {
        return (DiagnosticFactory3<E, A, B, C>) super.getFactory();
    }

    @NotNull
    public A getA() {
        return a;
    }

    @NotNull
    public B getB() {
        return b;
    }

    @NotNull
    public C getC() {
        return c;
    }

    @Override
    public String toString() {
        return getFactory() + "(a = " + a + ", b = " + b + ", c = " + c + ")";
    }
}
