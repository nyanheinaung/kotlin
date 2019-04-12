/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaCallableMemberDescriptor
import org.jetbrains.kotlin.resolve.MemberComparator
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedMemberScope
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase
import org.jetbrains.kotlin.test.util.DescriptorValidator
import org.jetbrains.kotlin.test.util.DescriptorValidator.ValidationVisitor

class DeserializedScopeValidationVisitor : ValidationVisitor() {
    override fun validateScope(scopeOwner: DeclarationDescriptor, scope: MemberScope, collector: DescriptorValidator.DiagnosticCollector) {
        super.validateScope(scopeOwner, scope, collector)
        validateDeserializedScope(scopeOwner, scope)
    }
}

private fun validateDeserializedScope(scopeOwner: DeclarationDescriptor, scope: MemberScope) {
    val isPackageViewScope = scopeOwner is PackageViewDescriptor
    if (scope is DeserializedMemberScope || isPackageViewScope) {
        val relevantDescriptors = scope.getContributedDescriptors().filter { member ->
            member is CallableMemberDescriptor && member.kind.isReal || (!isPackageViewScope && member is ClassDescriptor)
        }
        checkSorted(relevantDescriptors, scopeOwner)
    }
}

private fun checkSorted(descriptors: Collection<DeclarationDescriptor>, declaration: DeclarationDescriptor) {
    val serializedOnly = descriptors.filterNot { it is JavaCallableMemberDescriptor }
    KtUsefulTestCase.assertOrderedEquals(
            "Members of $declaration should be sorted by serialization.",
            serializedOnly,
            serializedOnly.sortedWith(MemberComparator.INSTANCE)
    )
}
