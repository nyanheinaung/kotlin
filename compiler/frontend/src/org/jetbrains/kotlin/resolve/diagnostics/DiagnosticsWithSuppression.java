/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.diagnostics;

import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.FilteringIterator;
import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.kotlin.diagnostics.Diagnostic;
import org.jetbrains.kotlin.resolve.BindingContext;

import java.util.Collection;
import java.util.Iterator;

public class DiagnosticsWithSuppression implements Diagnostics {
    private final KotlinSuppressCache kotlinSuppressCache;
    private final Collection<Diagnostic> diagnostics;
    private final DiagnosticsElementsCache elementsCache;

    public DiagnosticsWithSuppression(@NotNull BindingContext context, @NotNull Collection<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
        this.kotlinSuppressCache = new BindingContextSuppressCache(context);
        this.elementsCache = new DiagnosticsElementsCache(this, kotlinSuppressCache.getFilter());
    }

    @NotNull
    @Override
    public Diagnostics noSuppression() {
        return new SimpleDiagnostics(diagnostics);
    }

    @NotNull
    @Override
    public Iterator<Diagnostic> iterator() {
        return new FilteringIterator<>(diagnostics.iterator(), kotlinSuppressCache.getFilter()::invoke);
    }

    @NotNull
    @Override
    public Collection<Diagnostic> all() {
        return CollectionsKt.filter(diagnostics, kotlinSuppressCache.getFilter());
    }

    @NotNull
    @Override
    public Collection<Diagnostic> forElement(@NotNull PsiElement psiElement) {
        return elementsCache.getDiagnostics(psiElement);
    }

    @Override
    public boolean isEmpty() {
        return all().isEmpty();
    }

    @NotNull
    @Override
    public ModificationTracker getModificationTracker() {
        throw new IllegalStateException("Trying to obtain modification tracker for readonly DiagnosticsWithSuppression.");
    }

    @TestOnly
    @NotNull
    public Collection<Diagnostic> getDiagnostics() {
        return diagnostics;
    }
}
