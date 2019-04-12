/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.parsing.effects

import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.description.ReturnsEffectDeclaration
import org.jetbrains.kotlin.contracts.description.expressions.ConstantReference
import org.jetbrains.kotlin.contracts.parsing.*
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

internal class PsiReturnsEffectParser(
    collector: ContractParsingDiagnosticsCollector,
    callContext: ContractCallContext,
    contractParserDispatcher: PsiContractParserDispatcher
) : AbstractPsiEffectParser(collector, callContext, contractParserDispatcher) {
    override fun tryParseEffect(expression: KtExpression): EffectDeclaration? {
        val resolvedCall = expression.getResolvedCall(callContext.bindingContext) ?: return null
        val descriptor = resolvedCall.resultingDescriptor

        if (descriptor.isReturnsNotNullDescriptor()) return ReturnsEffectDeclaration(ConstantReference.NOT_NULL)
        if (descriptor.isReturnsWildcardDescriptor()) return ReturnsEffectDeclaration(ConstantReference.WILDCARD)

        if (!descriptor.isReturnsEffectDescriptor()) return null

        val argumentExpression = resolvedCall.firstArgumentAsExpressionOrNull()
        val constantValue = if (argumentExpression != null) contractParserDispatcher.parseConstant(argumentExpression) else null

        if (constantValue == null) {
            collector.badDescription(
                "only true/false/null constants in Returns-effect are currently supported",
                argumentExpression ?: expression
            )
            return null
        }

        return ReturnsEffectDeclaration(constantValue)
    }
}