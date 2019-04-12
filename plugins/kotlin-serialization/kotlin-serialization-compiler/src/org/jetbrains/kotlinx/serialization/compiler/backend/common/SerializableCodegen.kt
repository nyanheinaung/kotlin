/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.backend.common

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlinx.serialization.compiler.resolve.KSerializerDescriptorResolver
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializableProperties
import org.jetbrains.kotlinx.serialization.compiler.resolve.classSerializer
import org.jetbrains.kotlinx.serialization.compiler.resolve.isAbstractSerializableClass

abstract class SerializableCodegen(
    protected val serializableDescriptor: ClassDescriptor,
    bindingContext: BindingContext
) : AbstractSerialGenerator(bindingContext, serializableDescriptor) {
    protected val properties = SerializableProperties(serializableDescriptor, bindingContext)

    fun generate() {
        generateSyntheticInternalConstructor()
        generateSyntheticMethods()
    }

    private fun generateSyntheticInternalConstructor() {
        val serializerDescriptor = serializableDescriptor.classSerializer ?: return
        if (isAbstractSerializableClass(serializableDescriptor) || SerializerCodegen.getSyntheticLoadMember(serializerDescriptor) != null) {
            val constrDesc = KSerializerDescriptorResolver.createLoadConstructorDescriptor(serializableDescriptor, bindingContext)
            generateInternalConstructor(constrDesc)
        }
    }

    private fun generateSyntheticMethods() {
        val serializerDescriptor = serializableDescriptor.classSerializer ?: return
        if (isAbstractSerializableClass(serializableDescriptor) || SerializerCodegen.getSyntheticSaveMember(serializerDescriptor) != null) {
            val func = KSerializerDescriptorResolver.createWriteSelfFunctionDescriptor(serializableDescriptor)
            generateWriteSelfMethod(func)
        }
    }

    protected abstract fun generateInternalConstructor(constructorDescriptor: ClassConstructorDescriptor)

    protected open fun generateWriteSelfMethod(methodDescriptor: FunctionDescriptor) {

    }
}