/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.ConstraintPositionKind
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.derivedFrom
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import java.util.*

fun ConstraintSystem.getNestedTypeVariables(type: KotlinType): List<TypeVariable> {
    val nestedTypeParameters = type.getNestedTypeParameters().toSet()
    return typeVariables.filter { it.originalTypeParameter in nestedTypeParameters }
}

fun ConstraintSystem.filterConstraintsOut(excludePositionKind: ConstraintPositionKind): ConstraintSystem {
    return toBuilder { !it.derivedFrom(excludePositionKind) }.build()
}

fun ConstraintSystem.descriptorToVariable(call: CallHandle, descriptor: TypeParameterDescriptor): TypeVariable {
    return typeVariables.firstOrNull {
        it.call == call && it.originalTypeParameter == descriptor
    } ?: throw IllegalArgumentException("Unknown descriptor: $descriptor, call: $call")
}

internal fun KotlinType.getNestedArguments(): List<TypeProjection> {
    val result = ArrayList<TypeProjection>()

    val stack = ArrayDeque<TypeProjection>()
    stack.push(TypeProjectionImpl(this))

    while (!stack.isEmpty()) {
        val typeProjection = stack.pop()
        if (typeProjection.isStarProjection) continue

        result.add(typeProjection)

        typeProjection.type.arguments.forEach { stack.add(it) }
    }
    return result
}

internal fun KotlinType.getNestedTypeParameters(): List<TypeParameterDescriptor> {
    return getNestedArguments().mapNotNull { typeProjection ->
        typeProjection.type.constructor.declarationDescriptor as? TypeParameterDescriptor
    }
}
