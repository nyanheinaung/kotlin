/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.parsing.effects

import org.jetbrains.kotlin.contracts.description.CallsEffectDeclaration
import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.description.InvocationKind
import org.jetbrains.kotlin.contracts.parsing.*
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ExpressionValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.parents

internal class PsiCallsEffectParser(
    collector: ContractParsingDiagnosticsCollector,
    callContext: ContractCallContext,
    contractParserDispatcher: PsiContractParserDispatcher
) : AbstractPsiEffectParser(collector, callContext, contractParserDispatcher) {

    override fun tryParseEffect(expression: KtExpression): EffectDeclaration? {
        val resolvedCall = expression.getResolvedCall(callContext.bindingContext) ?: return null
        val descriptor = resolvedCall.resultingDescriptor

        if (!descriptor.isCallsInPlaceEffectDescriptor()) return null

        val lambda = contractParserDispatcher.parseVariable(resolvedCall.firstArgumentAsExpressionOrNull()) ?: return null

        val kindArgument = resolvedCall.valueArgumentsByIndex?.getOrNull(1)

        val kind = when (kindArgument) {
            is DefaultValueArgument -> InvocationKind.UNKNOWN
            is ExpressionValueArgument -> kindArgument.valueArgument?.getArgumentExpression()?.toInvocationKind(callContext.bindingContext)
            else -> null
        }

        if (kind == null) {
            val reportOn = (kindArgument as? ExpressionValueArgument)?.valueArgument?.getArgumentExpression() ?: expression
            collector.badDescription("unrecognized InvocationKind", reportOn)
            return null
        }

        return CallsEffectDeclaration(lambda, kind)
    }

    private fun KtExpression.toInvocationKind(bindingContext: BindingContext): InvocationKind? {
        val descriptor = this.getResolvedCall(bindingContext)?.resultingDescriptor ?: return null
        if (!descriptor.parents.first().isInvocationKindEnum()) return null

        return when (descriptor.fqNameSafe.shortName()) {
            ContractsDslNames.AT_MOST_ONCE_KIND -> InvocationKind.AT_MOST_ONCE
            ContractsDslNames.EXACTLY_ONCE_KIND -> InvocationKind.EXACTLY_ONCE
            ContractsDslNames.AT_LEAST_ONCE_KIND -> InvocationKind.AT_LEAST_ONCE
            ContractsDslNames.UNKNOWN_KIND -> InvocationKind.UNKNOWN
            else -> null
        }
    }
}