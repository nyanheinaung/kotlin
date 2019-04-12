/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class SimpleDiagnostic<E extends PsiElement> extends AbstractDiagnostic<E> {
    public SimpleDiagnostic(
            @NotNull E psiElement,
            @NotNull DiagnosticFactory0<E> factory,
            @NotNull Severity severity
    ) {
        super(psiElement, factory, severity);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public DiagnosticFactory0<E> getFactory() {
        return (DiagnosticFactory0<E>) super.getFactory();
    }
}
