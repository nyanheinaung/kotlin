/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorUtils
import java.util.*

open class AbstractAccessorForFunctionDescriptor(
        containingDeclaration: DeclarationDescriptor,
        name: Name
) : SimpleFunctionDescriptorImpl(containingDeclaration, null, Annotations.EMPTY,
                                 name, CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE) {

    protected fun copyTypeParameters(descriptor: FunctionDescriptor): List<TypeParameterDescriptor> = descriptor.typeParameters.map {
        val copy = TypeParameterDescriptorImpl.createForFurtherModification(
                this, it.annotations, it.isReified,
                it.variance, it.name,
                it.index, SourceElement.NO_SOURCE)
        for (upperBound in it.upperBounds) {
            copy.addUpperBound(upperBound)
        }
        copy.setInitialized()
        copy
    }

    protected fun copyValueParameters(descriptor: FunctionDescriptor): List<ValueParameterDescriptor> =
            descriptor.valueParameters.map { it.copy(this, it.name, it.index) }
}
