/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.components

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability

class TypeArgumentsToParametersMapper {

    sealed class TypeArgumentsMapping(val diagnostics: List<KotlinCallDiagnostic>) {

        abstract fun getTypeArgument(typeParameterDescriptor: TypeParameterDescriptor): TypeArgument

        object NoExplicitArguments : TypeArgumentsMapping(emptyList()) {
            override fun getTypeArgument(typeParameterDescriptor: TypeParameterDescriptor): TypeArgument =
                TypeArgumentPlaceholder
        }

        class TypeArgumentsMappingImpl(
            diagnostics: List<KotlinCallDiagnostic>,
            private val typeParameterToArgumentMap: Map<TypeParameterDescriptor, TypeArgument>
        ) : TypeArgumentsMapping(diagnostics) {
            override fun getTypeArgument(typeParameterDescriptor: TypeParameterDescriptor): TypeArgument =
                typeParameterToArgumentMap[typeParameterDescriptor] ?: TypeArgumentPlaceholder
        }
    }

    fun mapTypeArguments(call: KotlinCall, descriptor: CallableDescriptor): TypeArgumentsMapping {
        if (call.typeArguments.isEmpty()) {
            return TypeArgumentsMapping.NoExplicitArguments
        }

        if (call.typeArguments.size != descriptor.typeParameters.size) {
            return TypeArgumentsMapping.TypeArgumentsMappingImpl(
                listOf(WrongCountOfTypeArguments(descriptor, call.typeArguments.size)), emptyMap()
            )
        } else {
            val typeParameterToArgumentMap = descriptor.typeParameters.zip(call.typeArguments).associate { it }
            return TypeArgumentsMapping.TypeArgumentsMappingImpl(listOf(), typeParameterToArgumentMap)
        }
    }

}

