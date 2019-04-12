/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors.DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DelegationResolver
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.OverridingUtil

class DelegationChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is ClassDescriptor) return
        if (declaration !is KtClassOrObject) return

        for (specifier in declaration.superTypeListEntries) {
            if (specifier is KtDelegatedSuperTypeEntry) {
                val superType = specifier.typeReference?.let { context.trace.get(BindingContext.TYPE, it) } ?: continue
                val superTypeDescriptor = superType.constructor.declarationDescriptor as? ClassDescriptor ?: continue

                for ((delegated, delegatedTo) in DelegationResolver.getDelegates(descriptor, superTypeDescriptor)) {
                    checkDescriptor(declaration, delegated, delegatedTo, context.trace)
                }
            }
        }
    }

    private fun checkDescriptor(
        classDeclaration: KtClassOrObject,
        delegatedDescriptor: CallableMemberDescriptor,
        delegatedToDescriptor: CallableMemberDescriptor,
        diagnosticHolder: DiagnosticSink
    ) {
        val reachableFromDelegated =
            OverridingUtil.filterOutOverridden(
                DescriptorUtils.getAllOverriddenDescriptors(delegatedDescriptor).filter { it.kind.isReal }.toSet()
            ) - DescriptorUtils.unwrapFakeOverride(delegatedToDescriptor).original

        val nonAbstractReachable = reachableFromDelegated.filter { it.modality == Modality.OPEN }

        if (nonAbstractReachable.isNotEmpty()) {
            /*In case of MANY_IMPL_MEMBER_NOT_IMPLEMENTED error there could be several elements otherwise only one*/
            diagnosticHolder.report(
                DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE.on(
                    classDeclaration,
                    delegatedDescriptor,
                    nonAbstractReachable
                )
            )
        }
    }
}
