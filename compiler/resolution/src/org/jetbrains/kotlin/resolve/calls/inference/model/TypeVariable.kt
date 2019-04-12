/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference.model

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.resolve.calls.model.LambdaKotlinCallArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.checker.NewTypeVariableConstructor
import org.jetbrains.kotlin.types.model.TypeVariableMarker


class TypeVariableTypeConstructor(private val builtIns: KotlinBuiltIns, val debugName: String) : TypeConstructor,
    NewTypeVariableConstructor {
    override fun getParameters(): List<TypeParameterDescriptor> = emptyList()
    override fun getSupertypes(): Collection<KotlinType> = emptyList()
    override fun isFinal(): Boolean = false
    override fun isDenotable(): Boolean = false
    override fun getDeclarationDescriptor(): ClassifierDescriptor? = null

    override fun getBuiltIns() = builtIns

    override fun toString() = "TypeVariable($debugName)"
}

sealed class NewTypeVariable(builtIns: KotlinBuiltIns, name: String) : TypeVariableMarker {
    val freshTypeConstructor: TypeConstructor = TypeVariableTypeConstructor(builtIns, name)

    // member scope is used if we have receiver with type TypeVariable(T)
    // todo add to member scope methods from supertypes for type variable
    val defaultType: SimpleType = KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
        Annotations.EMPTY, freshTypeConstructor, arguments = emptyList(),
        nullable = false, memberScope = builtIns.any.unsubstitutedMemberScope
    )

    override fun toString() = freshTypeConstructor.toString()
}

class TypeVariableFromCallableDescriptor(
    val originalTypeParameter: TypeParameterDescriptor
) : NewTypeVariable(originalTypeParameter.builtIns, originalTypeParameter.name.identifier)

class TypeVariableForLambdaReturnType(
    val lambdaArgument: LambdaKotlinCallArgument,
    builtIns: KotlinBuiltIns,
    name: String
) : NewTypeVariable(builtIns, name)
