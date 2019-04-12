/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.descriptors

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyAccessorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl


interface IrSyntheticPropertyAccessorDescriptor : PropertyAccessorDescriptor {
    enum class Kind {
        STATIC_PROPERTY,
        MEMBER_PROPERTY
    }

    val kind: Kind
}

interface IrSyntheticPropertyGetterDescriptor : IrSyntheticPropertyAccessorDescriptor

interface IrSyntheticPropertySetterDescriptor : IrSyntheticPropertyAccessorDescriptor

class IrSyntheticPropertyGetterDescriptorImpl(
    correspondingProperty: PropertyDescriptor,
    override val kind: IrSyntheticPropertyAccessorDescriptor.Kind
) : PropertyGetterDescriptorImpl(
    correspondingProperty,
    Annotations.EMPTY,
    Modality.FINAL,
    correspondingProperty.visibility,
    true, // isDefault
    false, // isExternal
    false, // isInline
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    null,
    correspondingProperty.source
), IrSyntheticPropertyGetterDescriptor {
    init {
        initialize(correspondingProperty.type)
    }
}

class IrSyntheticPropertySetterDescriptorImpl(
    correspondingProperty: PropertyDescriptor,
    override val kind: IrSyntheticPropertyAccessorDescriptor.Kind
) : PropertySetterDescriptorImpl(
    correspondingProperty,
    Annotations.EMPTY,
    Modality.FINAL,
    correspondingProperty.visibility,
    true, // isDefault
    false, // isExternal
    false, // isInline
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    null,
    correspondingProperty.source
), IrSyntheticPropertySetterDescriptor {
    init {
        initializeDefault()
    }
}