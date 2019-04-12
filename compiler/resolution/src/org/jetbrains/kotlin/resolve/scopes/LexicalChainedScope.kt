/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorWithDeprecation
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.utils.takeSnapshot
import org.jetbrains.kotlin.util.collectionUtils.getFirstClassifierDiscriminateHeaders
import org.jetbrains.kotlin.util.collectionUtils.getFromAllScopes
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult

class LexicalChainedScope @JvmOverloads constructor(
    parent: LexicalScope,
    override val ownerDescriptor: DeclarationDescriptor,
    override val isOwnerDescriptorAccessibleByLabel: Boolean,
    override val implicitReceiver: ReceiverParameterDescriptor?,
    override val kind: LexicalScopeKind,
    // NB. Here can be very special subtypes of MemberScope (e.g., DeprecatedMemberScope).
    // Please, do not leak them outside of LexicalChainedScope, because other parts of compiler are not ready to work with them
    private val memberScopes: List<MemberScope>,
    @Deprecated("This value is temporary hack for resolve -- don't use it!")
    val isStaticScope: Boolean = false
) : LexicalScope {
    override val parent = parent.takeSnapshot()

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean) =
        getFromAllScopes(memberScopes) { it.getContributedDescriptors() }

    override fun getContributedClassifier(name: Name, location: LookupLocation) =
        getFirstClassifierDiscriminateHeaders(memberScopes) { it.getContributedClassifier(name, location) }

    override fun getContributedClassifierIncludeDeprecated(name: Name, location: LookupLocation): DescriptorWithDeprecation<ClassifierDescriptor>? {
        val (firstClassifier, isFirstDeprecated) = memberScopes.firstNotNullResult {
            it.getContributedClassifierIncludeDeprecated(name, location)
        } ?: return null

        if (!isFirstDeprecated) return DescriptorWithDeprecation.createNonDeprecated(firstClassifier)

        // Slow-path: try to find the same classifier, but without deprecation
        for (scope in memberScopes) {
            val (descriptor, isDeprecated) = scope.getContributedClassifierIncludeDeprecated(name, location) ?: continue
            if (descriptor == firstClassifier && !isDeprecated) return DescriptorWithDeprecation.createNonDeprecated(descriptor)
        }

        return DescriptorWithDeprecation.createDeprecated(firstClassifier)
    }

    override fun getContributedVariables(name: Name, location: LookupLocation) =
        getFromAllScopes(memberScopes) { it.getContributedVariables(name, location) }

    override fun getContributedFunctions(name: Name, location: LookupLocation) =
        getFromAllScopes(memberScopes) { it.getContributedFunctions(name, location) }

    override fun toString(): String = kind.toString()

    override fun printStructure(p: Printer) {
        p.println(
            this::class.java.simpleName, ": ", kind, "; for descriptor: ", ownerDescriptor.name,
            " with implicitReceiver: ", implicitReceiver?.value ?: "NONE", " {"
        )
        p.pushIndent()

        for (scope in memberScopes) {
            scope.printScopeStructure(p)
        }

        p.print("parent = ")
        parent.printStructure(p.withholdIndentOnce())

        p.popIndent()
        p.println("}")
    }

}
