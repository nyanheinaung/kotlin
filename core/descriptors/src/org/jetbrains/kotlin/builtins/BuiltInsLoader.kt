/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.deserialization.AdditionalClassPartsProvider
import org.jetbrains.kotlin.descriptors.deserialization.ClassDescriptorFactory
import org.jetbrains.kotlin.descriptors.deserialization.PlatformDependentDeclarationFilter
import org.jetbrains.kotlin.storage.StorageManager
import java.util.*

interface BuiltInsLoader {
    fun createPackageFragmentProvider(
        storageManager: StorageManager,
        builtInsModule: ModuleDescriptor,
        classDescriptorFactories: Iterable<ClassDescriptorFactory>,
        platformDependentDeclarationFilter: PlatformDependentDeclarationFilter,
        additionalClassPartsProvider: AdditionalClassPartsProvider,
        isFallback: Boolean
    ): PackageFragmentProvider

    companion object {
        val Instance: BuiltInsLoader by lazy(LazyThreadSafetyMode.PUBLICATION) {
            val implementations = ServiceLoader.load(BuiltInsLoader::class.java, BuiltInsLoader::class.java.classLoader)
            implementations.firstOrNull() ?: throw IllegalStateException(
                "No BuiltInsLoader implementation was found. Please ensure that the META-INF/services/ is not stripped " +
                        "from your application and that the Java virtual machine is not running under a security manager"
            )
        }
    }
}
