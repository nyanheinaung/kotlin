/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.descriptors.*

interface DescriptorsRemapper {
    fun remapDeclaredClass(descriptor: ClassDescriptor): ClassDescriptor = descriptor
    fun remapDeclaredConstructor(descriptor: ClassConstructorDescriptor): ClassConstructorDescriptor = descriptor
    fun remapDeclaredEnumEntry(descriptor: ClassDescriptor): ClassDescriptor = descriptor
    fun remapDeclaredExternalPackageFragment(descriptor: PackageFragmentDescriptor): PackageFragmentDescriptor = descriptor
    fun remapDeclaredField(descriptor: PropertyDescriptor): PropertyDescriptor = descriptor
    fun remapDeclaredFilePackageFragment(descriptor: PackageFragmentDescriptor): PackageFragmentDescriptor = descriptor
    fun remapDeclaredProperty(descriptor: PropertyDescriptor): PropertyDescriptor = descriptor
    fun remapDeclaredSimpleFunction(descriptor: FunctionDescriptor): FunctionDescriptor = descriptor
    fun remapDeclaredTypeParameter(descriptor: TypeParameterDescriptor): TypeParameterDescriptor = descriptor
    fun remapDeclaredValueParameter(descriptor: ParameterDescriptor): ParameterDescriptor = descriptor
    fun remapDeclaredVariable(descriptor: VariableDescriptor): VariableDescriptor = descriptor
    fun remapDeclaredLocalDelegatedProperty(descriptor: VariableDescriptorWithAccessors): VariableDescriptorWithAccessors = descriptor

    object Default : DescriptorsRemapper
}