/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.StorageManager

abstract class AbstractDeserializedPackageFragmentProvider(
    protected val storageManager: StorageManager,
    protected val finder: KotlinMetadataFinder,
    protected val moduleDescriptor: ModuleDescriptor
) : PackageFragmentProvider {
    protected lateinit var components: DeserializationComponents

    private val fragments = storageManager.createMemoizedFunctionWithNullableValues<FqName, PackageFragmentDescriptor> { fqName ->
        findPackage(fqName)?.apply {
            initialize(components)
        }
    }

    protected abstract fun findPackage(fqName: FqName): DeserializedPackageFragment?

    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> = listOfNotNull(fragments(fqName))

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> = emptySet()
}
