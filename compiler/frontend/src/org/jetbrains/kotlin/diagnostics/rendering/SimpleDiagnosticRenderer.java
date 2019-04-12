/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.diagnostics.Diagnostic;

public class SimpleDiagnosticRenderer implements DiagnosticRenderer<Diagnostic> {
    private final String message;

    public SimpleDiagnosticRenderer(@NotNull String message) {
        this.message = message;
    }

    @NotNull
    @Override
    public String render(@NotNull Diagnostic diagnostic) {
        return message;
    }
}
