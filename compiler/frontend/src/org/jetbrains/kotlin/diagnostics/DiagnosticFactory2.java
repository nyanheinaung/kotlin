/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DiagnosticFactory2<E extends PsiElement, A, B> extends DiagnosticFactoryWithPsiElement<E, DiagnosticWithParameters2<E, A, B>> {

    @NotNull
    public ParametrizedDiagnostic<E> on(@NotNull E element, @NotNull A a, @NotNull B b) {
        return new DiagnosticWithParameters2<>(element, a, b, this, getSeverity());
    }

    private DiagnosticFactory2(Severity severity, PositioningStrategy<? super E> positioningStrategy) {
        super(severity, positioningStrategy);
    }

    public static <T extends PsiElement, A, B> DiagnosticFactory2<T, A, B> create(Severity severity, PositioningStrategy<? super T> positioningStrategy) {
        return new DiagnosticFactory2<>(severity, positioningStrategy);
    }

    public static <T extends PsiElement, A, B> DiagnosticFactory2<T, A, B> create(Severity severity) {
        return new DiagnosticFactory2<>(severity, PositioningStrategies.DEFAULT);
    }

}
