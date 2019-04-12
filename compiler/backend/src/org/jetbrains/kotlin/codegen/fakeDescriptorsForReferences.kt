/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.serialization.DescriptorSerializer
import java.util.*

/**
 * Given a function descriptor, creates another function descriptor with type parameters copied from outer context(s).
 * This is needed because once we're serializing this to a proto, there's no place to store information about external type parameters.
 */
fun createFreeFakeLambdaDescriptor(descriptor: FunctionDescriptor): FunctionDescriptor {
    return createFreeDescriptor(descriptor)
}

private fun <D : CallableMemberDescriptor> createFreeDescriptor(descriptor: D): D {
    @Suppress("UNCHECKED_CAST")
    val builder = descriptor.newCopyBuilder() as CallableMemberDescriptor.CopyBuilder<D>

    val typeParameters = ArrayList<TypeParameterDescriptor>(0)
    builder.setTypeParameters(typeParameters)

    var container: DeclarationDescriptor? = descriptor.containingDeclaration
    while (container != null) {
        if (container is ClassDescriptor) {
            typeParameters.addAll(container.declaredTypeParameters)
        }
        else if (container is CallableDescriptor && container !is ConstructorDescriptor) {
            typeParameters.addAll(container.typeParameters)
        }
        container = container.containingDeclaration
    }

    return if (typeParameters.isEmpty()) descriptor else builder.build()!!
}

/**
 * Given a local delegated variable descriptor, creates a descriptor of a property that should be observed
 * when using reflection on that local variable at runtime.
 * Only members used by [DescriptorSerializer.propertyProto] are implemented correctly in this property descriptor.
 */
fun createFreeFakeLocalPropertyDescriptor(descriptor: LocalVariableDescriptor): PropertyDescriptor {
    val property = PropertyDescriptorImpl.create(
            descriptor.containingDeclaration, descriptor.annotations, Modality.FINAL, descriptor.visibility, descriptor.isVar,
            descriptor.name, CallableMemberDescriptor.Kind.DECLARATION, descriptor.source, false, descriptor.isConst,
            false, false, false, @Suppress("DEPRECATION") descriptor.isDelegated
    )
    property.setType(descriptor.type, descriptor.typeParameters, descriptor.dispatchReceiverParameter, descriptor.extensionReceiverParameter)

    property.initialize(
            descriptor.getter?.run {
                PropertyGetterDescriptorImpl(property, annotations, modality, visibility, true, isExternal, isInline, kind, null, source).apply {
                    initialize(this@run.returnType)
                }
            },
            descriptor.setter?.run {
                PropertySetterDescriptorImpl(property, annotations, modality, visibility, true, isExternal, isInline, kind, null, source).apply {
                    initialize(this@run.valueParameters.single())
                }
            }
    )

    return createFreeDescriptor(property)
}
