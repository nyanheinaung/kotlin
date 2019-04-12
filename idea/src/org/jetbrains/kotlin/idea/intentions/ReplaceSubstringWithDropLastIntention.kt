/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.evaluatesTo
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

class ReplaceSubstringWithDropLastIntention : ReplaceSubstringIntention("Replace 'substring' call with 'dropLast' call") {
    override fun applicabilityRangeInner(element: KtDotQualifiedExpression): TextRange? {
        val arguments = element.callExpression?.valueArguments ?: return null
        if (arguments.size != 2 || !element.isFirstArgumentZero()) return null

        val secondArgumentExpression = arguments[1].getArgumentExpression()

        if (secondArgumentExpression !is KtBinaryExpression) return null
        if (secondArgumentExpression.operationReference.getReferencedNameElementType() != KtTokens.MINUS) return null
        if (isLengthAccess(secondArgumentExpression.left, element.receiverExpression)) {
            return getTextRange(element)
        }

        return null
    }

    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
        val argument = element.callExpression!!.valueArguments[1].getArgumentExpression()!!
        val rightExpression = (argument as KtBinaryExpression).right!!

        element.replaceWith("$0.dropLast($1)", rightExpression)
    }

    private fun isLengthAccess(expression: KtExpression?, expectedReceiver: KtExpression): Boolean {
        return expression is KtDotQualifiedExpression
               && expression.selectorExpression.let { it is KtNameReferenceExpression && it.getReferencedName() == "length" }
               && expression.receiverExpression.evaluatesTo(expectedReceiver)
    }
}
