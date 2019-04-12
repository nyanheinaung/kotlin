/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class PrintingMessageCollector implements MessageCollector {
    private final boolean verbose;
    private final PrintStream errStream;
    private final MessageRenderer messageRenderer;
    private boolean hasErrors = false;

    public PrintingMessageCollector(@NotNull PrintStream errStream, @NotNull MessageRenderer messageRenderer, boolean verbose) {
        this.verbose = verbose;
        this.errStream = errStream;
        this.messageRenderer = messageRenderer;
    }

    @Override
    public void clear() {
        // Do nothing, messages are already reported
    }

    @Override
    public void report(
            @NotNull CompilerMessageSeverity severity,
            @NotNull String message,
            @Nullable CompilerMessageLocation location
    ) {
        if (!verbose && CompilerMessageSeverity.VERBOSE.contains(severity)) return;

        hasErrors |= severity.isError();

        errStream.println(messageRenderer.render(severity, message, location));
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }
}
