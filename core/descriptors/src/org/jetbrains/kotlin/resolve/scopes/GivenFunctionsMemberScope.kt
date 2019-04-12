/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.NonReportingOverrideStrategy
import org.jetbrains.kotlin.resolve.OverridingUtil
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.compact
import java.util.*

/**
 * A scope that may contain some declared functions + fake-overridden functions/properties from supertypes.
 */
abstract class GivenFunctionsMemberScope(
        storageManager: StorageManager,
        protected val containingClass: ClassDescriptor
) : MemberScopeImpl() {
    private val allDescriptors by storageManager.createLazyValue {
        val fromCurrent = computeDeclaredFunctions()
        fromCurrent + createFakeOverrides(fromCurrent)
    }

    protected abstract fun computeDeclaredFunctions(): List<FunctionDescriptor>

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): Collection<DeclarationDescriptor> {
        if (!kindFilter.acceptsKinds(DescriptorKindFilter.CALLABLES.kindMask)) return listOf()
        return allDescriptors
    }

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor> {
        return allDescriptors.filterIsInstance<SimpleFunctionDescriptor>().filter { it.name == name }
    }

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor> {
        return allDescriptors.filterIsInstance<PropertyDescriptor>().filter { it.name == name }
    }

    private fun createFakeOverrides(functionsFromCurrent: List<FunctionDescriptor>): List<DeclarationDescriptor> {
        val result = ArrayList<DeclarationDescriptor>(3)
        val allSuperDescriptors = containingClass.typeConstructor.supertypes
                .flatMap { it.memberScope.getContributedDescriptors() }
                .filterIsInstance<CallableMemberDescriptor>()
        for ((name, group) in allSuperDescriptors.groupBy { it.name }) {
            for ((isFunction, descriptors) in group.groupBy { it is FunctionDescriptor }) {
                OverridingUtil.generateOverridesInFunctionGroup(
                        name,
                        /* membersFromSupertypes = */ descriptors,
                        /* membersFromCurrent = */ if (isFunction) functionsFromCurrent.filter { it.name == name } else listOf(),
                        containingClass,
                        object : NonReportingOverrideStrategy() {
                            override fun addFakeOverride(fakeOverride: CallableMemberDescriptor) {
                                OverridingUtil.resolveUnknownVisibilityForMember(fakeOverride, null)
                                result.add(fakeOverride)
                            }

                            override fun conflict(fromSuper: CallableMemberDescriptor, fromCurrent: CallableMemberDescriptor) {
                                error("Conflict in scope of $containingClass: $fromSuper vs $fromCurrent")
                            }
                        }
                )
            }
        }

        return result.compact()
    }

    override fun printScopeStructure(p: Printer) {
        p.println("Scope of class: $containingClass")
    }
}
