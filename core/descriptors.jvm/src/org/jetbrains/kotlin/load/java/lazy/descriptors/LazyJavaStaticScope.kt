/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy.descriptors

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.load.java.lazy.LazyJavaResolverContext
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

abstract class LazyJavaStaticScope(c: LazyJavaResolverContext) : LazyJavaScope(c) {
    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = null

    override fun resolveMethodSignature(
        method: JavaMethod, methodTypeParameters: List<TypeParameterDescriptor>, returnType: KotlinType,
        valueParameters: List<ValueParameterDescriptor>
    ): MethodSignatureData =
        MethodSignatureData(returnType, null, valueParameters, methodTypeParameters, false, emptyList())

    override fun computeNonDeclaredProperties(name: Name, result: MutableCollection<PropertyDescriptor>) {
        //no undeclared properties
    }
}
