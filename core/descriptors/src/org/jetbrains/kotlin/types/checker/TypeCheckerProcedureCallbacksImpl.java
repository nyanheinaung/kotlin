/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;
import org.jetbrains.kotlin.types.TypeProjection;

class TypeCheckerProcedureCallbacksImpl implements TypeCheckingProcedureCallbacks {
    @Override
    public boolean assertEqualTypes(@NotNull KotlinType a, @NotNull KotlinType b, @NotNull TypeCheckingProcedure typeCheckingProcedure) {
        return typeCheckingProcedure.equalTypes(a, b);
    }

    @Override
    public boolean assertEqualTypeConstructors(@NotNull TypeConstructor a, @NotNull TypeConstructor b) {
        return a.equals(b);
    }

    @Override
    public boolean assertSubtype(@NotNull KotlinType subtype, @NotNull KotlinType supertype, @NotNull TypeCheckingProcedure typeCheckingProcedure) {
        return typeCheckingProcedure.isSubtypeOf(subtype, supertype);
    }

    @Override
    public boolean capture(@NotNull KotlinType type, @NotNull TypeProjection typeProjection) {
        return false;
    }

    @Override
    public boolean noCorrespondingSupertype(@NotNull KotlinType subtype, @NotNull KotlinType supertype) {
        return false; // type checking fails
    }
}
