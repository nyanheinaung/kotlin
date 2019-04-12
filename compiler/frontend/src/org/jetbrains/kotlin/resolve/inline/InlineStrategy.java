/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.inline;

public enum InlineStrategy {
    AS_FUNCTION,
    IN_PLACE,
    NOT_INLINE;

    public boolean isInline() {
        return this != NOT_INLINE;
    }
}
