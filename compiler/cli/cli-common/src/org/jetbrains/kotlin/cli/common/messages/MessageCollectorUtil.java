/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages;

import org.jetbrains.annotations.NotNull;

public class MessageCollectorUtil {
    public static void reportException(@NotNull MessageCollector messageCollector, @NotNull Throwable exception) {
        messageCollector.report(CompilerMessageSeverity.EXCEPTION, OutputMessageUtil.renderException(exception), null);
    }
}
