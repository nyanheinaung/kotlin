/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe

object JsMultipleInheritanceChecker : DeclarationChecker {
    private val fqNames = listOf(
            FqNameUnsafe("kotlin.CharSequence.get"),
            FqNameUnsafe("kotlin.collections.CharIterator.nextChar")
    )
    private val simpleNames = fqNames.mapTo(mutableSetOf()) { it.shortName() }

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is ClassDescriptor) return

        for (callable in descriptor.unsubstitutedMemberScope.getContributedDescriptors { it in simpleNames }
                .filterIsInstance<CallableMemberDescriptor>()) {
            if (callable.overriddenDescriptors.size > 1 && callable.overriddenDescriptors.any { it.fqNameUnsafe in fqNames }) {
                context.trace.report(ErrorsJs.WRONG_MULTIPLE_INHERITANCE.on(declaration, callable))
            }
        }
    }
}
