/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression

class ReplaceSubstringWithSubstringAfterIntention : ReplaceSubstringIntention("Replace 'substring' call with 'substringAfter' call") {
    override fun applicabilityRangeInner(element: KtDotQualifiedExpression): TextRange? {
        val arguments = element.callExpression?.valueArguments ?: return null

        if (arguments.size == 1 && isIndexOfCall(arguments[0].getArgumentExpression(), element.receiverExpression)) {
            return getTextRange(element)
        }

        return null
    }

    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
        element.replaceWith(
                "$0.substringAfter($1)",
                (element.getArgumentExpression(0) as KtDotQualifiedExpression).getArgumentExpression(0))
    }
}

class ReplaceSubstringWithSubstringBeforeIntention : ReplaceSubstringIntention("Replace 'substring' call with 'substringBefore' call") {
    override fun applicabilityRangeInner(element: KtDotQualifiedExpression): TextRange? {
        val arguments = element.callExpression?.valueArguments ?: return null

        if (arguments.size == 2
            && element.isFirstArgumentZero()
            && isIndexOfCall(arguments[1].getArgumentExpression(), element.receiverExpression)) {
            return getTextRange(element)
        }

        return null
    }

    override fun applyTo(element: KtDotQualifiedExpression, editor: Editor?) {
        element.replaceWith(
                "$0.substringBefore($1)",
                (element.getArgumentExpression(1) as KtDotQualifiedExpression).getArgumentExpression(0))
    }
}

private fun KtDotQualifiedExpression.getArgumentExpression(index: Int): KtExpression {
    return callExpression!!.valueArguments[index].getArgumentExpression()!!
}
