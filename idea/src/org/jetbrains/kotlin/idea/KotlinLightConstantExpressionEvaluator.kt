/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea

import com.intellij.psi.PsiConstantEvaluationHelper
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.ConstantExpressionEvaluator
import org.jetbrains.kotlin.asJava.elements.KtLightElementBase
import org.jetbrains.kotlin.idea.caches.resolve.getResolutionFacade
import org.jetbrains.kotlin.idea.project.languageVersionSettings
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator as FrontendConstantExpressionEvaluator

class KotlinLightConstantExpressionEvaluator : ConstantExpressionEvaluator {
    private fun evalConstantValue(constantValue: ConstantValue<*>): Any? {
        return if (constantValue is ArrayValue) {
            val items = constantValue.value.map { evalConstantValue(it) }
            items.singleOrNull() ?: items
        } else constantValue.value
    }

    override fun computeConstantExpression(expression: PsiElement, throwExceptionOnOverflow: Boolean): Any? {
        return computeExpression(expression, throwExceptionOnOverflow, null)
    }

    override fun computeExpression(
        expression: PsiElement,
        throwExceptionOnOverflow: Boolean,
        auxEvaluator: PsiConstantEvaluationHelper.AuxEvaluator?
    ): Any? {
        val expressionToCompute = when (expression) {
            is KtLightElementBase -> expression.kotlinOrigin as? KtExpression ?: return null
            else -> return null
        }

        val resolutionFacade = expressionToCompute.getResolutionFacade()
        val evaluator = FrontendConstantExpressionEvaluator(
            resolutionFacade.moduleDescriptor, expressionToCompute.languageVersionSettings, resolutionFacade.project
        )
        val evaluatorTrace = DelegatingBindingTrace(resolutionFacade.analyze(expressionToCompute), "Evaluating annotation argument")

        val constant = evaluator.evaluateExpression(expressionToCompute, evaluatorTrace) ?: return null
        if (constant.isError) return null
        return evalConstantValue(constant.toConstantValue(TypeUtils.NO_EXPECTED_TYPE))
    }
}
