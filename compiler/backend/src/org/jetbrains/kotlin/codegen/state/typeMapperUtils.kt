/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.state

import org.jetbrains.kotlin.codegen.AccessorForConstructorDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection

class ReceiverTypeAndTypeParameters(val receiverType: KotlinType, val typeParameters: List<TypeParameterDescriptor>)

fun patchTypeParametersForDefaultImplMethod(function: CallableMemberDescriptor): ReceiverTypeAndTypeParameters {
    val classDescriptor = function.containingDeclaration as ClassDescriptor
    val functionTypeParameterNames = function.typeParameters.map { it.name.asString() }
    val interfaceTypeParameters = classDescriptor.declaredTypeParameters
    val conflictedTypeParameters = interfaceTypeParameters.filter { it.name.asString() in functionTypeParameterNames }

    if (conflictedTypeParameters.isEmpty())
        return ReceiverTypeAndTypeParameters(classDescriptor.defaultType, interfaceTypeParameters)

    val existingNames = (functionTypeParameterNames + interfaceTypeParameters.map { it.name.asString() }).toMutableSet()

    val mappingForInterfaceTypeParameters = conflictedTypeParameters.associateBy({ it }) { typeParameter ->

        val newNamePrefix = typeParameter.name.asString() + "_I"
        val newName = newNamePrefix + generateSequence(1) { x -> x + 1 }.first { index ->
            (newNamePrefix + index) !in existingNames
        }

        existingNames.add(newName)
        function.createTypeParameterWithNewName(typeParameter, newName)
    }

    val substitution = TypeConstructorSubstitution.createByParametersMap(mappingForInterfaceTypeParameters.mapValues {
        it.value.defaultType.asTypeProjection()
    })

    val substitutor = TypeSubstitutor.create(substitution)

    val additionalTypeParameters = interfaceTypeParameters.map { typeParameter ->
        mappingForInterfaceTypeParameters[typeParameter] ?: typeParameter
    }
    val resultTypeParameters = mutableListOf<TypeParameterDescriptor>()
    DescriptorSubstitutor.substituteTypeParameters(additionalTypeParameters, substitution, classDescriptor, resultTypeParameters)

    return ReceiverTypeAndTypeParameters(substitutor.substitute(classDescriptor.defaultType, Variance.INVARIANT)!!, resultTypeParameters)
}

fun CallableMemberDescriptor.createTypeParameterWithNewName(
    descriptor: TypeParameterDescriptor,
    newName: String
): TypeParameterDescriptorImpl {
    val newDescriptor = TypeParameterDescriptorImpl.createForFurtherModification(
        this,
        descriptor.annotations,
        descriptor.isReified,
        descriptor.variance,
        Name.identifier(newName),
        descriptor.index,
        descriptor.source
    )
    descriptor.upperBounds.forEach {
        newDescriptor.addUpperBound(it)
    }
    newDescriptor.setInitialized()
    return newDescriptor
}

fun KotlinType.removeExternalProjections(): KotlinType {
    val newArguments = arguments.map { TypeProjectionImpl(Variance.INVARIANT, it.type) }
    return replace(newArguments)
}

fun isInlineClassConstructorAccessor(descriptor: FunctionDescriptor): Boolean =
    descriptor is AccessorForConstructorDescriptor &&
            descriptor.calleeDescriptor.constructedClass.isInline
