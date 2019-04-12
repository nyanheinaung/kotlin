/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.checker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeConstructor;

public class KotlinTypeCheckerImpl implements KotlinTypeChecker {


    @NotNull
    public static KotlinTypeChecker withAxioms(@NotNull final TypeConstructorEquality equalityAxioms) {
        return new KotlinTypeCheckerImpl(new TypeCheckingProcedure(new TypeCheckerProcedureCallbacksImpl() {
            @Override
            public boolean assertEqualTypeConstructors(@NotNull TypeConstructor constructor1, @NotNull TypeConstructor constructor2) {
                return constructor1.equals(constructor2) || equalityAxioms.equals(constructor1, constructor2);
            }
        }));
    }

    private final TypeCheckingProcedure procedure;

    protected KotlinTypeCheckerImpl(@NotNull TypeCheckingProcedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public boolean isSubtypeOf(@NotNull KotlinType subtype, @NotNull KotlinType supertype) {
        return procedure.isSubtypeOf(subtype, supertype);
    }

    @Override
    public boolean equalTypes(@NotNull KotlinType a, @NotNull KotlinType b) {
        return procedure.equalTypes(a, b);
    }

}
