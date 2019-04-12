/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.resolve.jvm.JavaDescriptorResolver
import javax.inject.Inject

interface ModuleClassResolver {
    fun resolveClass(javaClass: JavaClass): ClassDescriptor?
}

class SingleModuleClassResolver : ModuleClassResolver {
    override fun resolveClass(javaClass: JavaClass): ClassDescriptor? = resolver.resolveClass(javaClass)

    // component dependency cycle
    lateinit var resolver: JavaDescriptorResolver
        @Inject set
}

class ModuleClassResolverImpl(private val descriptorResolverByJavaClass: (JavaClass) -> JavaDescriptorResolver) : ModuleClassResolver {
    override fun resolveClass(javaClass: JavaClass): ClassDescriptor? = descriptorResolverByJavaClass(javaClass).resolveClass(javaClass)
}
