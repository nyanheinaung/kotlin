/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.evaluatesTo
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.isStableSimpleExpression
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

abstract class ReplaceSubstringIntention(text: String) : SelfTargetingRangeIntention<KtDotQualifiedExpression>(KtDotQualifiedExpression::class.java, text) {
    protected abstract fun applicabilityRangeInner(element: KtDotQualifiedExpression): TextRange?

    override fun applicabilityRange(element: KtDotQualifiedExpression): TextRange? {
        if (element.receiverExpression.isStableSimpleExpression() && element.isMethodCall("kotlin.text.substring")) {
            return applicabilityRangeInner(element)
        }
        return null
    }

    protected fun isIndexOfCall(expression: KtExpression?, expectedReceiver: KtExpression): Boolean {
        return expression is KtDotQualifiedExpression
               && expression.isMethodCall("kotlin.text.indexOf")
               && expression.receiverExpression.evaluatesTo(expectedReceiver)
               && expression.callExpression!!.valueArguments.size == 1
    }

    private fun KtDotQualifiedExpression.isMethodCall(fqMethodName: String): Boolean {
        val resolvedCall = toResolvedCall(BodyResolveMode.PARTIAL) ?: return false
        return resolvedCall.resultingDescriptor.fqNameUnsafe.asString() == fqMethodName
    }

    protected fun KtDotQualifiedExpression.isFirstArgumentZero(): Boolean {
        val bindingContext = analyze()
        val resolvedCall = callExpression.getResolvedCall(bindingContext) ?: return false
        val expression = resolvedCall.call.valueArguments[0].getArgumentExpression() as? KtConstantExpression ?: return false

        val constant = ConstantExpressionEvaluator.getConstant(expression, bindingContext) ?: return false
        val constantType = bindingContext.getType(expression) ?: return false
        return constant.getValue(constantType) == 0
    }

    protected fun getTextRange(element: KtDotQualifiedExpression): TextRange? {
        return element.callExpression?.textRange
    }

    protected fun KtDotQualifiedExpression.replaceWith(pattern: String, argument: KtExpression) {
        val psiFactory = KtPsiFactory(this)
        replace(psiFactory.createExpressionByPattern(pattern, receiverExpression, argument))
    }
}