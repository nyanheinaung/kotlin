/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.descriptors

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.Printer

class PredefinedPackageFragmentDescriptor(
        fqName: FqName,
        module: ModuleDescriptor,
        storageManager: StorageManager,
        val lazySubpackages: List<LazyAndroidExtensionsPackageFragmentDescriptor> = emptyList(),
        private val functions: (PredefinedPackageFragmentDescriptor) -> Collection<SimpleFunctionDescriptor> = { emptyList() }
) : PackageFragmentDescriptorImpl(module, fqName) {
    class LazyAndroidExtensionsPackageFragmentDescriptor(
        val descriptor: () -> PackageFragmentDescriptor,
        val isDeprecated: Boolean
    )

    private val calculatedFunctions = storageManager.createLazyValue {
        functions(this)
    }

    // Left for compatibility with Android Studio
    @Deprecated("Use lazySubpackages instead.", ReplaceWith("lazySubpackages"))
    @Suppress("unused")
    val subpackages: List<PackageFragmentDescriptor>
        get() = lazySubpackages.map { it.descriptor() }

    private val scope = PredefinedScope()
    
    override fun getMemberScope() = scope

    inner class PredefinedScope : MemberScopeImpl() {
        override fun getContributedVariables(name: Name, location: LookupLocation) = emptyList<PropertyDescriptor>()

        override fun getContributedFunctions(name: Name, location: LookupLocation) = calculatedFunctions().filter { it.name == name }

        override fun getContributedDescriptors(
                kindFilter: DescriptorKindFilter,
                nameFilter: (Name) -> Boolean
        ): List<SimpleFunctionDescriptor> {
            return calculatedFunctions().filter { nameFilter(it.name) && kindFilter.accepts(it) }
        }

        override fun printScopeStructure(p: Printer) {
            p.println(this::class.java.simpleName)
        }
    }
}
