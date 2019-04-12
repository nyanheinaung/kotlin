/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.builtins.jvm.JavaToKotlinClassMap
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.org.objectweb.asm.Type

interface JvmBackendClassResolver {
    fun resolveToClassDescriptors(type: Type): List<ClassDescriptor>

    object Dummy : JvmBackendClassResolver {
        override fun resolveToClassDescriptors(type: Type): List<ClassDescriptor> = emptyList()
    }
}


class JvmBackendClassResolverForModuleWithDependencies(
    private val moduleDescriptor: ModuleDescriptor
) : JvmBackendClassResolver {

    override fun resolveToClassDescriptors(type: Type): List<ClassDescriptor> {
        if (type.sort != Type.OBJECT) return emptyList()

        val className = type.className
        val lastDotIndex = className.lastIndexOf('.')
        val packageFQN = if (lastDotIndex >= 0) FqName(className.substring(0, lastDotIndex)) else FqName.ROOT
        val classRelativeNameWithDollars = if (lastDotIndex >= 0) className.substring(lastDotIndex + 1) else className
        val classFQN = FqName(classRelativeNameWithDollars.replace('$', '.'))
        val platformClass = moduleDescriptor.findClassAcrossModuleDependencies(ClassId(packageFQN, classFQN, false)) ?: return emptyList()

        return JavaToKotlinClassMap.mapPlatformClass(platformClass) + platformClass
    }
}
