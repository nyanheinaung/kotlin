/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtInstanceExpressionWithLabel
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.Receiver
import org.jetbrains.kotlin.resolve.scopes.utils.parentsWithSelf

object ConstructorHeaderCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val dispatchReceiverClass = resolvedCall.dispatchReceiver.classDescriptorForImplicitReceiver
        val extensionReceiverClass = resolvedCall.extensionReceiver.classDescriptorForImplicitReceiver

        val callElement = resolvedCall.call.callElement
        val labelReferenceClass =
            if (callElement is KtInstanceExpressionWithLabel) {
                context.trace.get(BindingContext.REFERENCE_TARGET, callElement.instanceReference) as? ClassDescriptor
            } else null

        if (dispatchReceiverClass == null && extensionReceiverClass == null && labelReferenceClass == null) return

        val classes = setOf(dispatchReceiverClass, extensionReceiverClass, labelReferenceClass)

        if (context.scope.parentsWithSelf.any { scope ->
                scope is LexicalScope && scope.kind == LexicalScopeKind.CONSTRUCTOR_HEADER &&
                        (scope.ownerDescriptor as ClassConstructorDescriptor).containingDeclaration in classes
            }) {
            context.trace.report(Errors.INSTANCE_ACCESS_BEFORE_SUPER_CALL.on(reportOn, resolvedCall.resultingDescriptor))
        }
    }
}

private val Receiver?.classDescriptorForImplicitReceiver: ClassDescriptor?
    get() = (this as? ImplicitReceiver)?.declarationDescriptor as? ClassDescriptor
