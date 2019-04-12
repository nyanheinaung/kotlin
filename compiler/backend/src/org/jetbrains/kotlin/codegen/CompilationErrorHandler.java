/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.kotlin.util.ExceptionUtilKt;

public interface CompilationErrorHandler {
    CompilationErrorHandler THROW_EXCEPTION = (exception, fileUrl) -> {
        throw new IllegalStateException(
                ExceptionUtilKt.getExceptionMessage("Backend", "Exception during code generation", exception, fileUrl),
                exception
        );
    };

    void reportException(Throwable exception, String fileUrl);
}
