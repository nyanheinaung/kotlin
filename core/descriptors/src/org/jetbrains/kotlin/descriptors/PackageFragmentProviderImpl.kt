/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class PackageFragmentProviderImpl(
        private val packageFragments: Collection<PackageFragmentDescriptor>
) : PackageFragmentProvider {
    override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
            packageFragments.filter { it.fqName == fqName }

    override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
            packageFragments.asSequence()
                    .map { it.fqName }
                    .filter { !it.isRoot && it.parent() == fqName }
                    .toList()
}
