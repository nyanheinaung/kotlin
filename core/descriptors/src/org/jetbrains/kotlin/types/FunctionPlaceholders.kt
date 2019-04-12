/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

class FunctionPlaceholders(private val builtIns: KotlinBuiltIns) {
    fun createFunctionPlaceholderType(
            argumentTypes: List<KotlinType>,
            hasDeclaredArguments: Boolean
    ): KotlinType {
        return ErrorUtils.createErrorTypeWithCustomConstructor(
                "function placeholder type",
                FunctionPlaceholderTypeConstructor(argumentTypes, hasDeclaredArguments, builtIns)
        )
    }
}

val KotlinType?.isFunctionPlaceholder: Boolean
    get() {
        return this != null && constructor is FunctionPlaceholderTypeConstructor
    }

class FunctionPlaceholderTypeConstructor(
        val argumentTypes: List<KotlinType>,
        val hasDeclaredArguments: Boolean,
        private val kotlinBuiltIns: KotlinBuiltIns
) : TypeConstructor {
    private val errorTypeConstructor: TypeConstructor = ErrorUtils.createErrorTypeConstructorWithCustomDebugName("PLACEHOLDER_FUNCTION_TYPE" + argumentTypes)

    override fun getParameters(): List<TypeParameterDescriptor> {
        return errorTypeConstructor.parameters
    }

    override fun getSupertypes(): Collection<KotlinType> {
        return errorTypeConstructor.supertypes
    }

    override fun isFinal(): Boolean {
        return errorTypeConstructor.isFinal
    }

    override fun isDenotable(): Boolean {
        return errorTypeConstructor.isDenotable
    }

    override fun getDeclarationDescriptor(): ClassifierDescriptor? {
        return errorTypeConstructor.declarationDescriptor
    }

    override fun toString(): String {
        return errorTypeConstructor.toString()
    }

    override fun getBuiltIns(): KotlinBuiltIns {
        return kotlinBuiltIns
    }
}
