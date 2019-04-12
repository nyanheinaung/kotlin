/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import java.util.*

class ConvertNegatedBooleanSequenceIntention : SelfTargetingOffsetIndependentIntention<KtBinaryExpression>(
        KtBinaryExpression::class.java, "Replace negated sequence with DeMorgan equivalent"
) {
    override fun isApplicableTo(element: KtBinaryExpression): Boolean {
        if (element.parent is KtBinaryExpression) return false // operate only on the longest sequence
        val opToken = element.operationToken
        if (opToken != KtTokens.ANDAND && opToken != KtTokens.OROR) return false

        return splitBooleanSequence(element) != null
    }

    override fun applyTo(element: KtBinaryExpression, editor: Editor?) {
        val operatorText = when(element.operationToken) {
            KtTokens.ANDAND -> KtTokens.OROR.value
            KtTokens.OROR -> KtTokens.ANDAND.value
            else -> throw IllegalArgumentException() // checked in isApplicableTo
        }

        val elements = splitBooleanSequence(element)!!
        val bareExpressions = elements.map { prefixExpression -> prefixExpression.baseExpression!!.text }
        val negatedExpression = bareExpressions.subList(0, bareExpressions.lastIndex).foldRight(
                "!(${bareExpressions.last()}", { negated, expression -> "$expression $operatorText $negated" }
        )

        val newExpression = KtPsiFactory(element).createExpression("$negatedExpression)")

        val insertedElement = element.replace(newExpression)
        val insertedElementParent = insertedElement.parent as? KtParenthesizedExpression ?: return

        if (KtPsiUtil.areParenthesesUseless(insertedElementParent)) {
            insertedElementParent.replace(insertedElement)
        }
    }

    private fun splitBooleanSequence(expression: KtBinaryExpression): List<KtPrefixExpression>? {
        val itemList = LinkedList<KtPrefixExpression>()
        val firstOperator = expression.operationToken

        var currentItem: KtBinaryExpression? = expression
        while (currentItem != null) {
            if (currentItem.operationToken != firstOperator) return null //Boolean sequence must be homogeneous

            val rightChild = currentItem.right as? KtPrefixExpression ?: return null
            itemList.add(rightChild)

            val leftChild = currentItem.left
            when (leftChild) {
                is KtPrefixExpression -> itemList.add(leftChild)
                !is KtBinaryExpression -> return null
            }

            currentItem = leftChild as? KtBinaryExpression
        }

        return itemList
    }

}
