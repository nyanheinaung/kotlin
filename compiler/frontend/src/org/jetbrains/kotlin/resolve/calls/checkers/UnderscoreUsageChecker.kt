/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall

object UnderscoreUsageChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (resolvedCall is VariableAsFunctionResolvedCall) return
        val descriptor = resolvedCall.resultingDescriptor
        val namedDescriptor: DeclarationDescriptor = (descriptor as? ConstructorDescriptor)?.containingDeclaration ?: descriptor
        if (!namedDescriptor.name.asString().isUnderscoreOnlyName()) return
        checkCallElement(resolvedCall.call.callElement, context)
    }

    private fun checkCallElement(ktElement: KtElement, context: CallCheckerContext) {
        when (ktElement) {
            is KtSimpleNameExpression ->
                checkSimpleNameUsage(ktElement, context.trace)
            is KtCallExpression ->
                ktElement.calleeExpression?.let { checkCallElement(it, context) }
        }
    }

    private fun checkSimpleNameUsage(ktName: KtSimpleNameExpression, trace: BindingTrace) {
        if (ktName.text.isUnderscoreOnlyName()) {
            trace.report(Errors.UNDERSCORE_USAGE_WITHOUT_BACKTICKS.on(ktName))
        }
    }

    fun checkSimpleNameUsage(descriptor: DeclarationDescriptor, ktName: KtSimpleNameExpression, trace: BindingTrace) {
        if (descriptor.name.asString().isUnderscoreOnlyName()) {
            checkSimpleNameUsage(ktName, trace)
        }
    }

    private fun String.isUnderscoreOnlyName() =
        isNotEmpty() && all { it == '_' }

}