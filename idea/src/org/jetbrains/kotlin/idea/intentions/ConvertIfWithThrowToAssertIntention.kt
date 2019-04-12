/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.isNullExpression
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.unwrapBlockOrParenthesis
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.inspections.SimplifyNegatedBinaryExpressionInspection
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.utils.addToStdlib.constant

class ConvertIfWithThrowToAssertIntention : SelfTargetingOffsetIndependentIntention<KtIfExpression>(KtIfExpression::class.java, "Replace 'if' with 'assert' statement") {
    override fun isApplicableTo(element: KtIfExpression): Boolean {
        if (element.`else` != null) return false

        val throwExpr = element.then?.unwrapBlockOrParenthesis() as? KtThrowExpression
        val thrownExpr = getSelector(throwExpr?.thrownExpression)
        if (thrownExpr !is KtCallExpression) return false

        if (thrownExpr.valueArguments.size > 1) return false

        val resolvedCall = thrownExpr.resolveToCall() ?: return false
        val targetFqName = DescriptorUtils.getFqName(resolvedCall.resultingDescriptor).asString()
        return targetFqName in constant { setOf("kotlin.AssertionError.<init>", "java.lang.AssertionError.<init>") }
    }

    override fun applyTo(element: KtIfExpression, editor: Editor?) {
        val condition = element.condition ?: return

        val thenExpr = element.then?.unwrapBlockOrParenthesis() as KtThrowExpression
        val thrownExpr = getSelector(thenExpr.thrownExpression) as KtCallExpression

        val psiFactory = KtPsiFactory(element)
        condition.replace(psiFactory.createExpressionByPattern("!$0", condition))

        var newCondition = element.condition!!
        val simplifier = SimplifyNegatedBinaryExpressionInspection()
        if (simplifier.isApplicable(newCondition as KtPrefixExpression)) {
            simplifier.applyTo(newCondition.operationReference, editor = editor)
            newCondition = element.condition!!
        }

        val arg = thrownExpr.valueArguments.singleOrNull()?.getArgumentExpression()
        val assertExpr = if (arg != null && !arg.isNullExpression())
            psiFactory.createExpressionByPattern("kotlin.assert($0) {$1}", newCondition, arg)
        else
            psiFactory.createExpressionByPattern("kotlin.assert($0)", newCondition)

        val newExpr = element.replaced(assertExpr)
        ShortenReferences.DEFAULT.process(newExpr)
    }

    private fun getSelector(element: KtExpression?): KtExpression? {
        if (element is KtDotQualifiedExpression) {
            return element.selectorExpression
        }
        return element
    }
}
