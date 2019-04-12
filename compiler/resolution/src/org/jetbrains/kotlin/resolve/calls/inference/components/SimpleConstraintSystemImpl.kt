/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference.components

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.components.ClassicTypeSystemContextForCS
import org.jetbrains.kotlin.resolve.calls.inference.ConstraintSystemBuilder
import org.jetbrains.kotlin.resolve.calls.inference.model.NewConstraintSystemImpl
import org.jetbrains.kotlin.resolve.calls.inference.model.SimpleConstraintSystemConstraintPosition
import org.jetbrains.kotlin.resolve.calls.inference.model.TypeVariableFromCallableDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.substitute
import org.jetbrains.kotlin.resolve.calls.results.SimpleConstraintSystem
import org.jetbrains.kotlin.types.TypeConstructorSubstitution
import org.jetbrains.kotlin.types.TypeSubstitutor
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection


class SimpleConstraintSystemImpl(constraintInjector: ConstraintInjector, builtIns: KotlinBuiltIns) : SimpleConstraintSystem {
    val csBuilder: ConstraintSystemBuilder =
        NewConstraintSystemImpl(constraintInjector, ClassicTypeSystemContextForCS(builtIns)).getBuilder()

    override fun registerTypeVariables(typeParameters: Collection<TypeParameterDescriptor>): TypeSubstitutor {
        val substitutionMap = typeParameters.associate {
            val variable = TypeVariableFromCallableDescriptor(it)
            csBuilder.registerVariable(variable)

            it.defaultType.constructor to variable.defaultType.asTypeProjection()
        }
        val substitutor = TypeConstructorSubstitution.createByConstructorsMap(substitutionMap).buildSubstitutor()
        for (typeParameter in typeParameters) {
            for (upperBound in typeParameter.upperBounds) {
                addSubtypeConstraint(substitutor.substitute(typeParameter.defaultType), substitutor.substitute(upperBound.unwrap()))
            }
        }
        return substitutor
    }

    override fun addSubtypeConstraint(subType: UnwrappedType, superType: UnwrappedType) {
        csBuilder.addSubtypeConstraint(subType, superType, SimpleConstraintSystemConstraintPosition)
    }

    override fun hasContradiction() = csBuilder.hasContradiction
    override val captureFromArgument get() = true
}