/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.resolve.calls.inference.TypeBounds.BoundKind
import org.jetbrains.kotlin.resolve.calls.inference.TypeBounds.BoundKind.*
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.ConstraintPosition
import org.jetbrains.kotlin.types.KotlinType

interface TypeBounds {
    val typeVariable: TypeVariable

    val bounds: Collection<Bound>

    val value: KotlinType?
        get() = if (values.size == 1) values.first() else null

    val values: Collection<KotlinType>

    enum class BoundKind {
        LOWER_BOUND,
        EXACT_BOUND,
        UPPER_BOUND
    }

    class Bound(
        val typeVariable: TypeVariable,
        val constrainingType: KotlinType,
        val kind: BoundKind,
        val position: ConstraintPosition,
        val isProper: Boolean,
        // to prevent infinite recursion in incorporation we store the variables that was substituted to derive this bound
        val derivedFrom: Set<TypeVariable>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.java != other::class.java) return false

            val bound = other as Bound

            if (typeVariable != bound.typeVariable) return false
            if (constrainingType != bound.constrainingType) return false
            if (kind != bound.kind) return false

            if (position.isStrong() != bound.position.isStrong()) return false

            return true
        }

        override fun hashCode(): Int {
            var result = typeVariable.hashCode()
            result = 31 * result + constrainingType.hashCode()
            result = 31 * result + kind.hashCode()
            result = 31 * result + if (position.isStrong()) 1 else 0
            return result
        }

        override fun toString() = "Bound($constrainingType, $kind, $position, isProper = $isProper)"
    }
}

fun BoundKind.reverse() = when (this) {
    LOWER_BOUND -> UPPER_BOUND
    UPPER_BOUND -> LOWER_BOUND
    EXACT_BOUND -> EXACT_BOUND
}
