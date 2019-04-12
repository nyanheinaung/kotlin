/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.deserialization

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType

interface AdditionalClassPartsProvider {
    fun getSupertypes(classDescriptor: ClassDescriptor): Collection<KotlinType>
    fun getFunctions(name: Name, classDescriptor: ClassDescriptor): Collection<SimpleFunctionDescriptor>
    fun getConstructors(classDescriptor: ClassDescriptor): Collection<ClassConstructorDescriptor>
    fun getFunctionsNames(classDescriptor: ClassDescriptor): Collection<Name>

    object None : AdditionalClassPartsProvider {
        override fun getSupertypes(classDescriptor: ClassDescriptor): Collection<KotlinType> = emptyList()
        override fun getFunctions(name: Name, classDescriptor: ClassDescriptor): Collection<SimpleFunctionDescriptor> = emptyList()
        override fun getFunctionsNames(classDescriptor: ClassDescriptor): Collection<Name> = emptyList()
        override fun getConstructors(classDescriptor: ClassDescriptor): Collection<ClassConstructorDescriptor> = emptyList()
    }
}
