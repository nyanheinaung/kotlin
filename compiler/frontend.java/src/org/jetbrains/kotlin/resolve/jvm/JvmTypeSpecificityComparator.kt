/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.asFlexibleType
import org.jetbrains.kotlin.types.isFlexible

object JvmTypeSpecificityComparator : TypeSpecificityComparator {

    override fun isDefinitelyLessSpecific(specific: KotlinType, general: KotlinType): Boolean {
        if (!specific.isFlexible() || general.isFlexible()) return false

        // general is inflexible
        val flexibility = specific.asFlexibleType()

        // For primitive types we have to take care of the case when there are two overloaded methods like
        //    foo(int) and foo(Integer)
        // if we do not discriminate one of them, any call to foo(kotlin.Int) will result in overload resolution ambiguity
        // so, for such cases, we discriminate Integer in favour of int
        if (!KotlinBuiltIns.isPrimitiveType(general) || !KotlinBuiltIns.isPrimitiveType(flexibility.lowerBound)) {
            return false
        }

        // Int? >< Int!
        if (general.isMarkedNullable) return false
        // Int! lessSpecific Int
        return true
    }
}