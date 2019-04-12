/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DiagnosticFactory1<E extends PsiElement, A> extends DiagnosticFactoryWithPsiElement<E, DiagnosticWithParameters1<E, A>> {
    @NotNull
    public ParametrizedDiagnostic<E> on(@NotNull E element, @NotNull A argument) {
        return new DiagnosticWithParameters1<>(element, argument, this, getSeverity());
    }

    protected DiagnosticFactory1(Severity severity, PositioningStrategy<? super E> positioningStrategy) {
        super(severity, positioningStrategy);
    }

    public static <T extends PsiElement, A> DiagnosticFactory1<T, A> create(Severity severity, PositioningStrategy<? super T> positioningStrategy) {
        return new DiagnosticFactory1<>(severity, positioningStrategy);
    }

    public static <T extends PsiElement, A> DiagnosticFactory1<T, A> create(Severity severity) {
        return create(severity, PositioningStrategies.DEFAULT);
    }
}
