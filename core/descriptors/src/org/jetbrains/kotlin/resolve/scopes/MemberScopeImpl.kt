/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.alwaysTrue

abstract class MemberScopeImpl : MemberScope {
    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? = null

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<@JvmWildcard PropertyDescriptor> =
        emptyList()

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<@JvmWildcard SimpleFunctionDescriptor> =
        emptyList()

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter,
                                           nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> = emptyList()

    override fun getFunctionNames(): Set<Name> =
            getContributedDescriptors(
                    DescriptorKindFilter.FUNCTIONS, alwaysTrue()
            ).filterIsInstance<SimpleFunctionDescriptor>().mapTo(mutableSetOf()) { it.name }

    override fun getVariableNames(): Set<Name> =
            getContributedDescriptors(
                    DescriptorKindFilter.VARIABLES, alwaysTrue()
            ).filterIsInstance<VariableDescriptor>().mapTo(mutableSetOf()) { it.name }

    override fun getClassifierNames(): Set<Name>? = null

    // This method should not be implemented here by default: every scope class has its unique structure pattern
    abstract override fun printScopeStructure(p: Printer)
}
