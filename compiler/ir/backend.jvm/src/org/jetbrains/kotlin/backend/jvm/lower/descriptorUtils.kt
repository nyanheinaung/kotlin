/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.lower

import org.jetbrains.kotlin.backend.common.lower.InitializersLowering
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name

fun FunctionDescriptor.toStatic(
    newOwner: ClassOrPackageFragmentDescriptor,
    name: Name = this.name,
    dispatchReceiverClass: ClassDescriptor? = this.containingDeclaration as? ClassDescriptor
): FunctionDescriptor {
    val newFunction = SimpleFunctionDescriptorImpl.create(
        newOwner, Annotations.EMPTY,
        name,
        CallableMemberDescriptor.Kind.DECLARATION, this.source
    )

    var offset = 0
    val dispatchReceiver = dispatchReceiverParameter?.let {
        ValueParameterDescriptorImpl.createWithDestructuringDeclarations(
            newFunction, null, offset++, Annotations.EMPTY, Name.identifier("this"),
            dispatchReceiverClass!!.defaultType, false, false, false, null, dispatchReceiverClass.source, null
        )
    }

    val extensionReceiver = extensionReceiverParameter?.let {
        ValueParameterDescriptorImpl.createWithDestructuringDeclarations(
            newFunction, null, offset++, Annotations.EMPTY, Name.identifier("receiver"),
            it.value.type, false, false, false, null, it.source, null
        )
    }

    val valueParameters = listOfNotNull(dispatchReceiver, extensionReceiver) +
            valueParameters.map { it.copy(newFunction, it.name, it.index + offset) }

    newFunction.initialize(
        null, null, emptyList()/*TODO: type parameters*/,
        valueParameters, returnType, Modality.FINAL, Visibilities.PUBLIC
    )
    return newFunction
}

fun FunctionDescriptor.isClInit(): Boolean = this.name == InitializersLowering.clinitName