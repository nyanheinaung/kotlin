/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.model.ConstraintStorage
import org.jetbrains.kotlin.resolve.calls.inference.model.NewTypeVariable
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.model.StubTypeMarker
import org.jetbrains.kotlin.types.model.TypeConstructorMarker
import org.jetbrains.kotlin.utils.addToStdlib.cast

fun ConstraintStorage.buildCurrentSubstitutor(additionalBindings: Map<TypeConstructorMarker, StubTypeMarker>): NewTypeSubstitutorByConstructorMap =
    NewTypeSubstitutorByConstructorMap( (fixedTypeVariables.entries.associate { it.key to it.value } + additionalBindings).cast() ) // TODO: SUB

fun ConstraintStorage.buildResultingSubstitutor(): NewTypeSubstitutor {
    val currentSubstitutorMap = fixedTypeVariables.entries.associate {
        it.key to it.value
    }
    val uninferredSubstitutorMap = notFixedTypeVariables.entries.associate { (freshTypeConstructor, typeVariable) ->
        freshTypeConstructor to ErrorUtils.createErrorTypeWithCustomConstructor(
            "Uninferred type",
            (typeVariable.typeVariable as NewTypeVariable).freshTypeConstructor// TODO: SUB
        )
    }

    return NewTypeSubstitutorByConstructorMap((currentSubstitutorMap + uninferredSubstitutorMap).cast()) // TODO: SUB
}

val CallableDescriptor.returnTypeOrNothing: UnwrappedType
    get() {
        returnType?.let { return it.unwrap() }

        return builtIns.nothingType
    }

fun TypeSubstitutor.substitute(type: UnwrappedType): UnwrappedType = safeSubstitute(type, Variance.INVARIANT).unwrap()

fun CallableDescriptor.substitute(substitutor: NewTypeSubstitutor): CallableDescriptor {
    val wrappedSubstitution = object : TypeSubstitution() {
        override fun get(key: KotlinType): TypeProjection? = null
        override fun prepareTopLevelType(topLevelType: KotlinType, position: Variance) = substitutor.safeSubstitute(topLevelType.unwrap())
    }
    return substitute(TypeSubstitutor.create(wrappedSubstitution))
}

fun CallableDescriptor.substituteAndApproximateCapturedTypes(substitutor: NewTypeSubstitutor): CallableDescriptor {
    val wrappedSubstitution = object : TypeSubstitution() {
        override fun get(key: KotlinType): TypeProjection? = null

        override fun prepareTopLevelType(topLevelType: KotlinType, position: Variance) =
            substitutor.safeSubstitute(topLevelType.unwrap()).let { substitutedType ->
                TypeApproximator(builtIns).approximateToSuperType(substitutedType, TypeApproximatorConfiguration.CapturedAndIntegerLiteralsTypesApproximation)
                        ?: substitutedType
            }
    }

    return substitute(TypeSubstitutor.create(wrappedSubstitution))
}

internal fun <E> MutableList<E>.trimToSize(newSize: Int) = subList(newSize, size).clear()