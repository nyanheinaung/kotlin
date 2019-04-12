/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.SourceFile
import org.jetbrains.kotlin.load.java.descriptors.getImplClassNameForDeserialized
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaPackageFragment
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedMemberDescriptor

class KotlinJvmBinaryPackageSourceElement(
    private val packageFragment: LazyJavaPackageFragment
) : SourceElement {
    override fun toString() = "$packageFragment: ${packageFragment.binaryClasses.keys}"

    override fun getContainingFile(): SourceFile = SourceFile.NO_SOURCE_FILE

    fun getRepresentativeBinaryClass(): KotlinJvmBinaryClass {
        return packageFragment.binaryClasses.values.first()
    }

    fun getContainingBinaryClass(descriptor: DeserializedMemberDescriptor): KotlinJvmBinaryClass? {
        val name = descriptor.getImplClassNameForDeserialized() ?: return null
        return packageFragment.binaryClasses[name.internalName]
    }
}
