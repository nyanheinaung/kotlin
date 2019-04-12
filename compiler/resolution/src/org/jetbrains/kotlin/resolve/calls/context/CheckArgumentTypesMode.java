/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.context;

public enum CheckArgumentTypesMode {
    /**
     * Check value argument types for particular call.
     */
    CHECK_VALUE_ARGUMENTS,
    /**
     * Match callable reference type against expected (callable) type.
     */
    CHECK_CALLABLE_TYPE
}
