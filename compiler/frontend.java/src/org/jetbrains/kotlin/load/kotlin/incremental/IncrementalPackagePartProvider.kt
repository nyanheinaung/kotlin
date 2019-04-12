/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin.incremental

import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCache
import org.jetbrains.kotlin.load.kotlin.loadModuleMapping
import org.jetbrains.kotlin.metadata.jvm.deserialization.ModuleMapping
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.serialization.deserialization.DeserializationConfiguration
import org.jetbrains.kotlin.storage.StorageManager

class IncrementalPackagePartProvider(
        private val parent: PackagePartProvider,
        incrementalCaches: List<IncrementalCache>,
        storageManager: StorageManager
) : PackagePartProvider {
    lateinit var deserializationConfiguration: DeserializationConfiguration

    private val moduleMappings = storageManager.createLazyValue {
        incrementalCaches.map { cache ->
            ModuleMapping.loadModuleMapping(cache.getModuleMappingData(), "<incremental>", deserializationConfiguration) { version ->
                // Incremental compilation should fall back to full rebuild if the minor component of the metadata version has changed
                throw IllegalStateException("Version of the generated module should not be incompatible: $version")
            }
        }
    }

    override fun findPackageParts(packageFqName: String): List<String> {
        return (moduleMappings().mapNotNull { it.findPackageParts(packageFqName) }.flatMap { it.parts } +
                parent.findPackageParts(packageFqName)).distinct()
    }

    override fun getAnnotationsOnBinaryModule(moduleName: String): List<ClassId> {
        return parent.getAnnotationsOnBinaryModule(moduleName)
    }
}
