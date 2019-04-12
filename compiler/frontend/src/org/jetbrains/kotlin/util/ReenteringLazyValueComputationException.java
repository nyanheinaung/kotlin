/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

public class ReenteringLazyValueComputationException extends RuntimeException {
    public ReenteringLazyValueComputationException() {
    }

    @NotNull
    @Override
    public synchronized Throwable fillInStackTrace() {
        Application application = ApplicationManager.getApplication();
        if (application == null || application.isInternal() || application.isUnitTestMode()) {
            return super.fillInStackTrace();
        }
        return this;
    }
}
