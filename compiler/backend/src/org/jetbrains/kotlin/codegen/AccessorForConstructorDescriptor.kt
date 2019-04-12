/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

class AccessorForConstructorDescriptor(
    override val calleeDescriptor: ClassConstructorDescriptor,
    containingDeclaration: DeclarationDescriptor,
    override val superCallTarget: ClassDescriptor?,
    override val accessorKind: AccessorKind
) : AbstractAccessorForFunctionDescriptor(containingDeclaration, Name.special("<init>")),
    ClassConstructorDescriptor,
    AccessorForCallableDescriptor<ConstructorDescriptor> {

    override fun getContainingDeclaration(): ClassDescriptor = calleeDescriptor.containingDeclaration

    override fun getConstructedClass(): ClassDescriptor = calleeDescriptor.constructedClass

    override fun isPrimary(): Boolean = false

    override fun getReturnType(): KotlinType = super.getReturnType()!!


    override fun substitute(substitutor: TypeSubstitutor) = super.substitute(substitutor) as ClassConstructorDescriptor

    override fun copy(
        newOwner: DeclarationDescriptor,
        modality: Modality,
        visibility: Visibility,
        kind: CallableMemberDescriptor.Kind,
        copyOverrides: Boolean
    ): AccessorForConstructorDescriptor {
        throw UnsupportedOperationException()
    }

    override fun getOriginal(): AccessorForConstructorDescriptor = this

    init {
        initialize(
            calleeDescriptor.extensionReceiverParameter?.copy(this),
            calleeDescriptor.dispatchReceiverParameter,
            copyTypeParameters(calleeDescriptor),
            copyValueParameters(calleeDescriptor),
            calleeDescriptor.returnType,
            Modality.FINAL,
            Visibilities.LOCAL
        )
    }
}
