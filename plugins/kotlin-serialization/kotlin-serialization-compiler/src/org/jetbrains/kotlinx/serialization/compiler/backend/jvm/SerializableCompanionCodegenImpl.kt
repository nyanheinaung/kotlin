/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.backend.jvm

import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlinx.serialization.compiler.backend.common.SerializableCompanionCodegen
import org.jetbrains.kotlinx.serialization.compiler.resolve.classSerializer
import org.jetbrains.kotlinx.serialization.compiler.resolve.getSerializableClassDescriptorByCompanion
import org.jetbrains.kotlinx.serialization.compiler.resolve.shouldHaveGeneratedMethodsInCompanion

class SerializableCompanionCodegenImpl(private val classCodegen: ImplementationBodyCodegen) :
    SerializableCompanionCodegen(classCodegen.descriptor, classCodegen.bindingContext) {

    companion object {
        fun generateSerializableExtensions(codegen: ImplementationBodyCodegen) {
            val serializableClass = getSerializableClassDescriptorByCompanion(codegen.descriptor) ?: return
            if (serializableClass.shouldHaveGeneratedMethodsInCompanion)
                SerializableCompanionCodegenImpl(codegen).generate()
        }
    }

    override fun generateSerializerGetter(methodDescriptor: FunctionDescriptor) {
        val serial = serializableDescriptor.classSerializer ?: return
        classCodegen.generateMethod(methodDescriptor) { _, _ ->
            stackValueSerializerInstance(
                classCodegen,
                serializableDescriptor.module,
                serializableDescriptor.defaultType,
                serial,
                this,
                null
            ) {
                load(it + 1, kSerializerType)
            }
            areturn(kSerializerType)
        }
    }
}