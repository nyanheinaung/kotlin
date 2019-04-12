/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg;

public enum TailRecursionKind {
    TAIL_CALL(true),
    IN_TRY(false),
    NON_TAIL(false);

    private final boolean doGenerateTailRecursion;

    TailRecursionKind(boolean doGenerateTailRecursion) {
        this.doGenerateTailRecursion = doGenerateTailRecursion;
    }

    public boolean isDoGenerateTailRecursion() {
        return doGenerateTailRecursion;
    }
}
