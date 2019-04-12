/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.types.checker.ErrorTypesAreEqualToAnything

/**
 * This is temporary hack for type intersector.
 *
 * It is almost save, because:
 *  - it running only if general algorithm is failed
 *  - returned type is subtype of all [types].
 *
 * But it is hack, because it can give unstable result, but it better than exception.
 * See KT-11266.
 */
internal fun hackForTypeIntersector(types: Collection<KotlinType>): KotlinType? {
    if (types.size < 2) return types.firstOrNull()

    return types.firstOrNull { candidate ->
        types.all {
            ErrorTypesAreEqualToAnything.isSubtypeOf(candidate, it)
        }
    }
}

fun getEffectiveVariance(parameterVariance: Variance, projectionKind: Variance): Variance {
    if (parameterVariance === Variance.INVARIANT) {
        return projectionKind
    }
    if (projectionKind === Variance.INVARIANT) {
        return parameterVariance
    }
    if (parameterVariance === projectionKind) {
        return parameterVariance
    }

    // In<out X> = In<*>
    // Out<in X> = Out<*>
    return Variance.OUT_VARIANCE
}