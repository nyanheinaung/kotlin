/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.idea.core.replaced
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis

abstract class ReplaceMathMethodsWithKotlinNativeMethodsIntention(
        text: String, private val replacedMethodName: String, private val mathMethodName: String
) : SelfTargetingOffsetIndependentIntention<KtCallExpression>(KtCallExpression::class.java, text) {

    override fun applyTo(element: KtCallExpression, editor: Editor?) {
        val target = element.getQualifiedExpressionForSelectorOrThis()
        val valueArguments = element.valueArguments
        val methodName = replacedMethodName
        val newExpression = KtPsiFactory(element).createExpressionByPattern("$0.$methodName($1)",
                                                                            valueArguments[0].text, valueArguments[1].text)
        target.replaced(newExpression)
    }

    override fun isApplicableTo(element: KtCallExpression) =
            element.calleeExpression?.text == mathMethodName &&
            element.valueArguments.size == 2 &&
            element.isMethodCall("java.lang.Math.$mathMethodName")
}