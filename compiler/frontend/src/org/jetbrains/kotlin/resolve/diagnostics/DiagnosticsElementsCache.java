/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.diagnostics;

import com.intellij.openapi.util.AtomicNotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ConcurrentMultiMap;
import com.intellij.util.containers.MultiMap;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.Diagnostic;

import java.util.Collection;

public class DiagnosticsElementsCache {
    private final Diagnostics diagnostics;
    private final Function1<Diagnostic, Boolean> filter;

    private final AtomicNotNullLazyValue<MultiMap<PsiElement, Diagnostic>> elementToDiagnostic = new AtomicNotNullLazyValue<MultiMap<PsiElement, Diagnostic>>() {
        @NotNull
        @Override
        protected MultiMap<PsiElement, Diagnostic> compute() {
            return buildElementToDiagnosticCache(diagnostics, filter);
        }
    };

    public DiagnosticsElementsCache(Diagnostics diagnostics, Function1<Diagnostic, Boolean> filter) {
        this.diagnostics = diagnostics;
        this.filter = filter;
    }

    @NotNull
    public Collection<Diagnostic> getDiagnostics(@NotNull PsiElement psiElement) {
        return elementToDiagnostic.getValue().get(psiElement);
    }

    private static MultiMap<PsiElement, Diagnostic> buildElementToDiagnosticCache(Diagnostics diagnostics, Function1<Diagnostic, Boolean> filter) {
        MultiMap<PsiElement, Diagnostic> elementToDiagnostic = new ConcurrentMultiMap<>();
        for (Diagnostic diagnostic : diagnostics) {
            if (diagnostic == null) {
                throw new IllegalStateException(
                        "There shouldn't be null diagnostics in the collection: " + CollectionsKt.toList(diagnostics));
            }
            if (filter.invoke(diagnostic)) {
                elementToDiagnostic.putValue(diagnostic.getPsiElement(), diagnostic);
            }
        }

        return elementToDiagnostic;
    }
}
