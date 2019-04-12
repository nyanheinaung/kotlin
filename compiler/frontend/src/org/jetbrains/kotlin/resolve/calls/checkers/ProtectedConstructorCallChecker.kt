/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtConstructorDelegationReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.parentsWithSelf

object ProtectedConstructorCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val descriptor = resolvedCall.resultingDescriptor as? ConstructorDescriptor ?: return
        val constructorOwner = descriptor.containingDeclaration.original
        val scopeOwner = context.scope.ownerDescriptor

        val actualConstructor = (descriptor as? TypeAliasConstructorDescriptor)?.underlyingConstructorDescriptor ?: descriptor

        if (actualConstructor.visibility.normalize() != Visibilities.PROTECTED) return
        // Error already reported
        if (!Visibilities.isVisibleWithAnyReceiver(descriptor, scopeOwner)) return

        val calleeExpression = resolvedCall.call.calleeExpression

        // Permit constructor super-calls
        when (calleeExpression) {
            is KtConstructorCalleeExpression -> if (calleeExpression.parent is KtSuperTypeCallEntry) return
            is KtConstructorDelegationReferenceExpression -> return
        }

        // Permit calls within class
        if (scopeOwner.parentsWithSelf.any { it.original === constructorOwner }) return

        // Using FALSE_IF_PROTECTED helps us to check that descriptor doesn't meet conditions of java package/static-protected
        // (i.e. being in the same package)
        // And without ProtectedConstructorCallChecker such calls would be allowed only because they are performed within subclass
        // of constructor owner
        @Suppress("DEPRECATION")
        if (Visibilities.findInvisibleMember(Visibilities.FALSE_IF_PROTECTED, descriptor, scopeOwner) == actualConstructor.original) {
            context.trace.report(Errors.PROTECTED_CONSTRUCTOR_NOT_IN_SUPER_CALL.on(reportOn, descriptor))
        }
    }
}
