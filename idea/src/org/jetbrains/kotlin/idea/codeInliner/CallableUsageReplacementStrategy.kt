/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInliner

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.intentions.OperatorToFunctionIntention
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

class CallableUsageReplacementStrategy(
    private val replacement: CodeToInline,
    private val inlineSetter: Boolean = false
) : UsageReplacementStrategy {
    override fun createReplacer(usage: KtSimpleNameExpression): (() -> KtElement?)? {
        val bindingContext = usage.analyze(BodyResolveMode.PARTIAL_WITH_CFA)
        val resolvedCall = usage.getResolvedCall(bindingContext) ?: return null
        if (!resolvedCall.status.isSuccess) return null

        val callElement = when (resolvedCall) {
            is VariableAsFunctionResolvedCall -> resolvedCall.variableCall.call.callElement
            else -> resolvedCall.call.callElement

        }

        if (callElement !is KtExpression && callElement !is KtAnnotationEntry) {
            return null
        }

        //TODO: precheck pattern correctness for annotation entry

        return {
            if (usage is KtOperationReferenceExpression && usage.getReferencedNameElementType() != KtTokens.IDENTIFIER) {
                val nameExpression = OperatorToFunctionIntention.convert(usage.parent as KtExpression).second
                createReplacer(nameExpression)?.invoke()
            } else {
                CodeInliner(usage, bindingContext, resolvedCall, callElement, inlineSetter, replacement).doInline()
            }
        }
    }
}

