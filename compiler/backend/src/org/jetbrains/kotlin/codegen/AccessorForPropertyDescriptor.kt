/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.types.KotlinType

open class AccessorForPropertyDescriptor private constructor(
    override val calleeDescriptor: PropertyDescriptor,
    propertyType: KotlinType,
    receiverType: KotlinType?,
    dispatchReceiverParameter: ReceiverParameterDescriptor?,
    containingDeclaration: DeclarationDescriptor,
    override val superCallTarget: ClassDescriptor?,
    val accessorSuffix: String,
    val isWithSyntheticGetterAccessor: Boolean,
    val isWithSyntheticSetterAccessor: Boolean,
    final override val accessorKind: AccessorKind
) : PropertyDescriptorImpl(
    containingDeclaration,
    null,
    Annotations.EMPTY,
    Modality.FINAL,
    Visibilities.LOCAL,
    calleeDescriptor.isVar,
    Name.identifier("access$$accessorSuffix"),
    CallableMemberDescriptor.Kind.DECLARATION,
    SourceElement.NO_SOURCE,
    false,
    false,
    false,
    false,
    false,
    false
), AccessorForCallableDescriptor<PropertyDescriptor> {

    constructor(
        property: PropertyDescriptor,
        containingDeclaration: DeclarationDescriptor,
        superCallTarget: ClassDescriptor?,
        nameSuffix: String,
        getterAccessorRequired: Boolean,
        setterAccessorRequired: Boolean,
        accessorKind: AccessorKind
    ) : this(
        property, property.type, DescriptorUtils.getReceiverParameterType(property.extensionReceiverParameter),
        /* dispatchReceiverParameter = */
        if (property.isJvmStaticInObjectOrClassOrInterface()) null else property.dispatchReceiverParameter,
        containingDeclaration, superCallTarget, nameSuffix,
        getterAccessorRequired, setterAccessorRequired,
        accessorKind
    )

    protected constructor(
        original: PropertyDescriptor,
        propertyType: KotlinType,
        receiverType: KotlinType?,
        dispatchReceiverParameter: ReceiverParameterDescriptor?,
        containingDeclaration: DeclarationDescriptor,
        superCallTarget: ClassDescriptor?,
        nameSuffix: String,
        accessorKind: AccessorKind
    ) : this(
        original,
        propertyType,
        receiverType,
        dispatchReceiverParameter,
        containingDeclaration,
        superCallTarget,
        nameSuffix,
        true,
        true,
        accessorKind
    )

    init {
        setType(
            propertyType, emptyList<TypeParameterDescriptorImpl>(), dispatchReceiverParameter,
            DescriptorFactory.createExtensionReceiverParameterForCallable(this, receiverType, Annotations.EMPTY)
        )

        val getterDescriptor =
            if (isWithSyntheticGetterAccessor) Getter(this, accessorKind) else calleeDescriptor.getter as PropertyGetterDescriptorImpl?
        val setterDescriptor = if (isWithSyntheticSetterAccessor) Setter(this, accessorKind) else calleeDescriptor.setter
        initialize(getterDescriptor, setterDescriptor)
    }

    class Getter(property: AccessorForPropertyDescriptor, override val accessorKind: AccessorKind) : PropertyGetterDescriptorImpl(
        property,
        Annotations.EMPTY,
        Modality.FINAL,
        Visibilities.LOCAL,
        false,
        false,
        false,
        CallableMemberDescriptor.Kind.DECLARATION,
        null,
        SourceElement.NO_SOURCE
    ), AccessorForCallableDescriptor<PropertyGetterDescriptor> {

        override val calleeDescriptor: PropertyGetterDescriptor
            get() = (correspondingProperty as AccessorForPropertyDescriptor).calleeDescriptor.getter!!

        override val superCallTarget: ClassDescriptor?
            get() = (correspondingProperty as AccessorForPropertyDescriptor).superCallTarget

        init {
            initialize(property.type)
        }

    }

    class Setter(property: AccessorForPropertyDescriptor, override val accessorKind: AccessorKind) : PropertySetterDescriptorImpl(
        property,
        Annotations.EMPTY,
        Modality.FINAL,
        Visibilities.LOCAL,
        false,
        false,
        false,
        CallableMemberDescriptor.Kind.DECLARATION,
        null,
        SourceElement.NO_SOURCE
    ), AccessorForCallableDescriptor<PropertySetterDescriptor> {

        override val calleeDescriptor: PropertySetterDescriptor
            get() = (correspondingProperty as AccessorForPropertyDescriptor).calleeDescriptor.setter!!

        override val superCallTarget: ClassDescriptor?
            get() = (correspondingProperty as AccessorForPropertyDescriptor).superCallTarget

        init {
            initializeDefault()
        }
    }
}
