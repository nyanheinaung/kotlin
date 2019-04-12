/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy

import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaPackageFragment
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.CacheWithNotNullValues

class LazyJavaPackageFragmentProvider(
    components: JavaResolverComponents
) : PackageFragmentProvider {

    private val c = LazyJavaResolverContext(components, TypeParameterResolver.EMPTY, lazyOf(null))

    private val packageFragments: CacheWithNotNullValues<FqName, LazyJavaPackageFragment> =
        c.storageManager.createCacheWithNotNullValues()

    private fun getPackageFragment(fqName: FqName): LazyJavaPackageFragment? {
        val jPackage = c.components.finder.findPackage(fqName) ?: return null

        return packageFragments.computeIfAbsent(fqName) {
            LazyJavaPackageFragment(c, jPackage)
        }
    }

    override fun getPackageFragments(fqName: FqName) = listOfNotNull(getPackageFragment(fqName))

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean) =
        getPackageFragment(fqName)?.getSubPackageFqNames().orEmpty()
}
