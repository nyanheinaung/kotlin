/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;
import org.jetbrains.kotlin.types.TypeProjection;

/**
 * Methods of this class return true to continue type checking and false to fail
 */
public interface TypeCheckingProcedureCallbacks {
    boolean assertEqualTypes(@NotNull KotlinType a, @NotNull KotlinType b, @NotNull TypeCheckingProcedure typeCheckingProcedure);

    boolean assertEqualTypeConstructors(@NotNull TypeConstructor a, @NotNull TypeConstructor b);

    boolean assertSubtype(@NotNull KotlinType subtype, @NotNull KotlinType supertype, @NotNull TypeCheckingProcedure typeCheckingProcedure);

    boolean capture(@NotNull KotlinType type, @NotNull TypeProjection typeProjection);

    boolean noCorrespondingSupertype(@NotNull KotlinType subtype, @NotNull KotlinType supertype);
}
