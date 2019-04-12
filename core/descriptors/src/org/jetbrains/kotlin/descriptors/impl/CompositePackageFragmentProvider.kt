/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.name.FqName
import java.util.*
import org.jetbrains.kotlin.name.Name

class CompositePackageFragmentProvider(// can be modified from outside
        private val providers: List<PackageFragmentProvider>) : PackageFragmentProvider {

    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
        val result = ArrayList<PackageFragmentDescriptor>()
        for (provider in providers) {
            result.addAll(provider.getPackageFragments(fqName))
        }
        return result.toList()
    }

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> {
        val result = HashSet<FqName>()
        for (provider in providers) {
            result.addAll(provider.getSubPackagesOf(fqName, nameFilter))
        }
        return result
    }
}
