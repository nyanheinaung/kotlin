/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.impl.SubpackagesScope
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.Printer

class SubpackagesImportingScope(
    override val parent: ImportingScope?,
    moduleDescriptor: ModuleDescriptor,
    fqName: FqName
) : SubpackagesScope(moduleDescriptor, fqName), ImportingScope by ImportingScope.Empty {

    override fun getContributedPackage(name: Name): PackageViewDescriptor? = getPackage(name)

    override fun printStructure(p: Printer) = printScopeStructure(p)

    override fun getContributedVariables(name: Name, location: LookupLocation) = super.getContributedVariables(name, location)
    override fun getContributedFunctions(name: Name, location: LookupLocation) = super.getContributedFunctions(name, location)

    //TODO: kept old behavior, but it seems very strange (super call seems more applicable)
    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? = null

    override fun getContributedDescriptors(
        kindFilter: DescriptorKindFilter,
        nameFilter: (Name) -> Boolean
    ): Collection<DeclarationDescriptor> =
        emptyList()

    //TODO: kept old behavior, but it seems very strange (super call seems more applicable)
    override fun getContributedDescriptors(
        kindFilter: DescriptorKindFilter,
        nameFilter: (Name) -> Boolean,
        changeNamesForAliased: Boolean
    ): Collection<DeclarationDescriptor> = emptyList()

    override fun computeImportedNames() = emptySet<Name>()
}
