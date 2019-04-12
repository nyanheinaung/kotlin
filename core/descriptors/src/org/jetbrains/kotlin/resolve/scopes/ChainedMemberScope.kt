/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.collectionUtils.getFirstClassifierDiscriminateHeaders
import org.jetbrains.kotlin.util.collectionUtils.getFromAllScopes
import org.jetbrains.kotlin.utils.Printer

class ChainedMemberScope(
        internal val debugName: String,
        private val scopes: List<MemberScope>
) : MemberScope {
    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor?
            = getFirstClassifierDiscriminateHeaders(scopes) { it.getContributedClassifier(name, location) }

    override fun getContributedVariables(name: Name, location: LookupLocation): Collection<PropertyDescriptor>
            = getFromAllScopes(scopes) { it.getContributedVariables(name, location) }

    override fun getContributedFunctions(name: Name, location: LookupLocation): Collection<SimpleFunctionDescriptor>
            = getFromAllScopes(scopes) { it.getContributedFunctions(name, location) }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean)
            = getFromAllScopes(scopes) { it.getContributedDescriptors(kindFilter, nameFilter) }

    override fun getFunctionNames() = scopes.flatMapTo(mutableSetOf()) { it.getFunctionNames() }
    override fun getVariableNames() = scopes.flatMapTo(mutableSetOf()) { it.getVariableNames() }
    override fun getClassifierNames(): Set<Name>? = scopes.flatMapClassifierNamesOrNull()

    override fun recordLookup(name: Name, location: LookupLocation) {
        scopes.forEach { it.recordLookup(name, location) }
    }

    override fun toString() = debugName

    override fun printScopeStructure(p: Printer) {
        p.println(this::class.java.simpleName, ": ", debugName, " {")
        p.pushIndent()

        for (scope in scopes) {
            scope.printScopeStructure(p)
        }

        p.popIndent()
        p.println("}")
    }

    companion object {
        fun create(debugName: String, scopes: List<MemberScope>): MemberScope {
            return when (scopes.size) {
                0 -> MemberScope.Empty
                1 -> scopes.single()
                else -> ChainedMemberScope(debugName, scopes)
            }
        }
    }
}
