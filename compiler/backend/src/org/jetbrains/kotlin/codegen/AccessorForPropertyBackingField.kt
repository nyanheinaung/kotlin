/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType

class AccessorForPropertyBackingField(
    property: PropertyDescriptor,
    containingDeclaration: DeclarationDescriptor,
    delegateType: KotlinType?,
    extensionReceiverParameter: ReceiverParameterDescriptor?,
    dispatchReceiverParameter: ReceiverParameterDescriptor?,
    nameSuffix: String,
    fieldAccessorKind: AccessorKind
) : AccessorForPropertyDescriptor(
    property,
    delegateType ?: property.type,
    extensionReceiverParameter?.type,
    dispatchReceiverParameter,
    containingDeclaration,
    null,
    nameSuffix,
    fieldAccessorKind
)
