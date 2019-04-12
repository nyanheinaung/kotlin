/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.parsing.effects

import org.jetbrains.kotlin.contracts.description.ConditionalEffectDeclaration
import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.parsing.*
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal class PsiConditionalEffectParser(
    collector: ContractParsingDiagnosticsCollector,
    callContext: ContractCallContext,
    dispatcher: PsiContractParserDispatcher
) : AbstractPsiEffectParser(collector, callContext, dispatcher) {
    override fun tryParseEffect(expression: KtExpression): EffectDeclaration? {
        val resolvedCall = expression.getResolvedCall(callContext.bindingContext) ?: return null
        if (!resolvedCall.resultingDescriptor.isImpliesCallDescriptor()) return null

        val effect = contractParserDispatcher.parseEffect(resolvedCall.dispatchReceiver.safeAs<ExpressionReceiver>()?.expression)
                ?: return null
        val condition = contractParserDispatcher.parseCondition(resolvedCall.firstArgumentAsExpressionOrNull())
                ?: return null

        return ConditionalEffectDeclaration(effect, condition)
    }
}