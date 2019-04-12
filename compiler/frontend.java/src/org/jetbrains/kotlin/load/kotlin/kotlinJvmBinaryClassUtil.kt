/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaPackageFragment
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedClassDescriptor
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CallableDescriptor.getContainingKotlinJvmBinaryClass(): KotlinJvmBinaryClass? {
    if (this !is DeserializedCallableMemberDescriptor) return null

    val container = containingDeclaration

    return when (container) {
        is DeserializedClassDescriptor ->
            container.source.safeAs<KotlinJvmBinarySourceElement>()?.binaryClass
        is LazyJavaPackageFragment ->
            container.source.safeAs<KotlinJvmBinaryPackageSourceElement>()?.getRepresentativeBinaryClass()
        else -> null
    }
}
