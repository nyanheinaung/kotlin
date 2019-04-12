/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class PrintingLogger extends Logger {

    public static final Logger SYSTEM_OUT = new PrintingLogger(System.out);
    public static final Logger SYSTEM_ERR = new PrintingLogger(System.err);

    private final PrintStream out;

    public PrintingLogger(@NotNull PrintStream out) {
        this.out = out;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(@NonNls String message) {
        out.println(message);
    }

    @Override
    public void debug(@Nullable Throwable t) {
        //noinspection ConstantConditions
        t.printStackTrace(out);
    }

    @Override
    public void debug(@NonNls String message, @Nullable Throwable t) {
        debug(message);
        debug(t);
    }

    @Override
    public void info(@NonNls String message) {
        debug(message);
    }

    @Override
    public void info(@NonNls String message, @Nullable Throwable t) {
        debug(message, t);
    }

    @Override
    public void warn(@NonNls String message, @Nullable Throwable t) {
        debug(message, t);
    }

    @Override
    public void error(@NonNls String message, @Nullable Throwable t, @NonNls @NotNull String... details) {
        debug(message, t);
        for (String detail : details) {
            debug(detail);
        }
    }

    @Override
    public void setLevel(Level level) {
    }
}
