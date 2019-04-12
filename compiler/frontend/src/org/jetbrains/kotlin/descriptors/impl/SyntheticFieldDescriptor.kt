/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.source.toSourceElement

class SyntheticFieldDescriptor private constructor(
    val propertyDescriptor: PropertyDescriptor,
    accessorDescriptor: PropertyAccessorDescriptor,
    property: KtProperty
) : LocalVariableDescriptor(
    accessorDescriptor, Annotations.EMPTY, SyntheticFieldDescriptor.NAME,
    propertyDescriptor.type, propertyDescriptor.isVar, false, false,
    property.toSourceElement()
) {

    constructor(
        accessorDescriptor: PropertyAccessorDescriptor,
        property: KtProperty
    ) : this(accessorDescriptor.correspondingProperty, accessorDescriptor, property)

    override fun getDispatchReceiverParameter() = null

    fun getDispatchReceiverForBackend() = getDispatchReceiverParameterForBackend()?.value

    fun getDispatchReceiverParameterForBackend() = propertyDescriptor.dispatchReceiverParameter

    companion object {
        @JvmField
        val NAME = Name.identifier("field")
    }
}

val DeclarationDescriptor.referencedProperty: PropertyDescriptor?
    get() = when (this) {
        is SyntheticFieldDescriptor -> this.propertyDescriptor
        is PropertyDescriptor -> this
        else -> null
    }
