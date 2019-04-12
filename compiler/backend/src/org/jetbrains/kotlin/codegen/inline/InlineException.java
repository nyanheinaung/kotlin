/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline;

public class InlineException extends RuntimeException {

    public InlineException(String message) {
        super(message);
    }

    public InlineException(String message, Throwable cause) {
        super(message, cause);
    }
}
