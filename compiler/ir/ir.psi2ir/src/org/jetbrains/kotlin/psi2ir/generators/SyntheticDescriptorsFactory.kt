/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.PropertyGetterDescriptor
import org.jetbrains.kotlin.descriptors.PropertySetterDescriptor
import org.jetbrains.kotlin.ir.descriptors.IrSyntheticPropertyAccessorDescriptor.Kind.MEMBER_PROPERTY
import org.jetbrains.kotlin.ir.descriptors.IrSyntheticPropertyAccessorDescriptor.Kind.STATIC_PROPERTY
import org.jetbrains.kotlin.ir.descriptors.IrSyntheticPropertyGetterDescriptorImpl
import org.jetbrains.kotlin.ir.descriptors.IrSyntheticPropertySetterDescriptorImpl
import java.lang.AssertionError
import java.util.*


class SyntheticDescriptorsFactory {
    private val propertyGetters = HashMap<PropertyDescriptor, PropertyGetterDescriptor>()

    private fun generateGetter(property: PropertyDescriptor): PropertyGetterDescriptor {
        return when {
            isStaticPropertyInClass(property) ->
                IrSyntheticPropertyGetterDescriptorImpl(property, STATIC_PROPERTY)
            isPropertyInClass(property) ->
                IrSyntheticPropertyGetterDescriptorImpl(property, MEMBER_PROPERTY)
            else ->
                throw AssertionError("Don't know how to create synthetic getter for $property")
        }
    }

    private val propertySetters = HashMap<PropertyDescriptor, PropertySetterDescriptor>()

    private fun generateSetter(property: PropertyDescriptor): PropertySetterDescriptor {
        return when {
            isStaticPropertyInClass(property) ->
                IrSyntheticPropertySetterDescriptorImpl(property, STATIC_PROPERTY)
            isPropertyInClass(property) ->
                IrSyntheticPropertySetterDescriptorImpl(property, MEMBER_PROPERTY)
            else ->
                throw AssertionError("Don't know how to create synthetic setter for $property")
        }
    }

    private fun isStaticPropertyInClass(propertyDescriptor: PropertyDescriptor): Boolean =
        propertyDescriptor.containingDeclaration is ClassDescriptor &&
                propertyDescriptor.dispatchReceiverParameter == null &&
                propertyDescriptor.extensionReceiverParameter == null

    private fun isPropertyInClass(propertyDescriptor: PropertyDescriptor): Boolean =
        propertyDescriptor.containingDeclaration is ClassDescriptor

    fun getOrCreatePropertyGetter(propertyDescriptor: PropertyDescriptor): PropertyGetterDescriptor =
        propertyGetters.getOrPut(propertyDescriptor) { generateGetter(propertyDescriptor) }

    fun getOrCreatePropertySetter(propertyDescriptor: PropertyDescriptor): PropertySetterDescriptor =
        propertySetters.getOrPut(propertyDescriptor) { generateSetter(propertyDescriptor) }
}
