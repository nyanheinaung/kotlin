/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public abstract class DiagnosticFactory<D extends Diagnostic> {

    private String name = null;
    private final Severity severity;

    protected DiagnosticFactory(@NotNull Severity severity) {
        this.severity = severity;
    }

    protected DiagnosticFactory(@NotNull String name, @NotNull Severity severity) {
        this.name = name;
        this.severity = severity;
    }

    /*package*/ void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Severity getSeverity() {
        return severity;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public D cast(@NotNull Diagnostic diagnostic) {
        if (diagnostic.getFactory() != this) {
            throw new IllegalArgumentException("Factory mismatch: expected " + this + " but was " + diagnostic.getFactory());
        }

        return (D) diagnostic;
    }

    @NotNull
    @SafeVarargs
    public static <D extends Diagnostic> D cast(@NotNull Diagnostic diagnostic, @NotNull DiagnosticFactory<? extends D>... factories) {
        return cast(diagnostic, Arrays.asList(factories));
    }

    @NotNull
    public static <D extends Diagnostic> D cast(@NotNull Diagnostic diagnostic, @NotNull Collection<? extends DiagnosticFactory<? extends D>> factories) {
        for (DiagnosticFactory<? extends D> factory : factories) {
            if (diagnostic.getFactory() == factory) return factory.cast(diagnostic);
        }

        throw new IllegalArgumentException("Factory mismatch: expected one of " + factories + " but was " + diagnostic.getFactory());
    }

    @Override
    public String toString() {
        return getName();
    }
}
