/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.selectMostSpecificInEachOverridableGroup
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.Printer

class TypeIntersectionScope private constructor(override val workerScope: ChainedMemberScope) : AbstractScopeAdapter() {
    override fun getContributedFunctions(name: Name, location: LookupLocation) =
            super.getContributedFunctions(name, location).selectMostSpecificInEachOverridableGroup { this }

    override fun getContributedVariables(name: Name, location: LookupLocation) =
            super.getContributedVariables(name, location).selectMostSpecificInEachOverridableGroup { this }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> {
        val (callables, other) = super.getContributedDescriptors(kindFilter, nameFilter).partition { it is CallableDescriptor }

        @Suppress("UNCHECKED_CAST")
        return (callables as Collection<CallableDescriptor>).selectMostSpecificInEachOverridableGroup { this } + other
    }

    override fun printScopeStructure(p: Printer) {
        p.print("TypeIntersectionScope for: " + workerScope.debugName)
        super.printScopeStructure(p)
    }

    companion object {
        @JvmStatic
        fun create(message: String, types: Collection<KotlinType>): MemberScope {
            val chainedScope = ChainedMemberScope(message, types.map { it.memberScope })
            if (types.size <= 1) return chainedScope

            return TypeIntersectionScope(chainedScope)
        }
    }
}
