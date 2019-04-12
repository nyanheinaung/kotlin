/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.resolve

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlinx.serialization.compiler.backend.common.analyzeSpecialSerializers

class SerializableProperty(
    val descriptor: PropertyDescriptor,
    val isConstructorParameterWithDefault: Boolean,
    hasBackingField: Boolean
) {
    val name = descriptor.annotations.serialNameValue ?: descriptor.name.asString()
    val type = descriptor.type
    val genericIndex = type.genericIndex
    val module = descriptor.module
    val serializableWith = descriptor.serializableWith ?: analyzeSpecialSerializers(module, descriptor.annotations)?.defaultType
    val optional = !descriptor.annotations.serialRequired && descriptor.declaresDefaultValue
    val transient = descriptor.annotations.serialTransient || !hasBackingField
    val annotationsWithArguments: List<Triple<ClassDescriptor, List<ValueArgument>, List<ValueParameterDescriptor>>> =
        descriptor.annotationsWithArguments()
}

val PropertyDescriptor.declaresDefaultValue: Boolean
    get() = when (val declaration = this.source.getPsi()) {
        is KtDeclarationWithInitializer -> declaration.initializer != null
        is KtParameter -> declaration.defaultValue != null
        else -> false
    }