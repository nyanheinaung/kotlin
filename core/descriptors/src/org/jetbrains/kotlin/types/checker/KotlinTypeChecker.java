/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;

public interface KotlinTypeChecker {

    interface TypeConstructorEquality {
        boolean equals(@NotNull TypeConstructor a, @NotNull TypeConstructor b);
    }

    KotlinTypeChecker DEFAULT = NewKotlinTypeChecker.INSTANCE;

    boolean isSubtypeOf(@NotNull KotlinType subtype, @NotNull KotlinType supertype);
    boolean equalTypes(@NotNull KotlinType a, @NotNull KotlinType b);
}
