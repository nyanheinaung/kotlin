/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindExclude
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addIfNotNull
import java.util.*

open class SubpackagesScope(private val moduleDescriptor: ModuleDescriptor, private val fqName: FqName) : MemberScopeImpl() {

    protected fun getPackage(name: Name): PackageViewDescriptor? {
        if (name.isSpecial) {
            return null
        }
        val packageViewDescriptor = moduleDescriptor.getPackage(fqName.child(name))
        if (packageViewDescriptor.isEmpty()) {
            return null
        }
        return packageViewDescriptor
    }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter,
                                           nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> {
        if (!kindFilter.acceptsKinds(DescriptorKindFilter.PACKAGES_MASK)) return listOf()
        if (fqName.isRoot && kindFilter.excludes.contains(DescriptorKindExclude.TopLevelPackages)) return listOf()

        val subFqNames = moduleDescriptor.getSubPackagesOf(fqName, nameFilter)
        val result = ArrayList<DeclarationDescriptor>(subFqNames.size)
        for (subFqName in subFqNames) {
            val shortName = subFqName.shortName()
            if (nameFilter(shortName)) {
                result.addIfNotNull(getPackage(shortName))
            }
        }
        return result
    }

    override fun getClassifierNames(): Set<Name> = emptySet()

    override fun printScopeStructure(p: Printer) {
        p.println(this::class.java.simpleName, " {")
        p.pushIndent()

        p.popIndent()
        p.println("}")
    }
}
