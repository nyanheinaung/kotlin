/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.util.CommentSaver
import org.jetbrains.kotlin.psi.*

class RemoveUnnecessaryParenthesesIntention : SelfTargetingRangeIntention<KtParenthesizedExpression>(
    KtParenthesizedExpression::class.java, "Remove unnecessary parentheses"
) {
    override fun applicabilityRange(element: KtParenthesizedExpression): TextRange? {
        element.expression ?: return null
        if (!KtPsiUtil.areParenthesesUseless(element)) return null
        return element.textRange
    }

    override fun applyTo(element: KtParenthesizedExpression, editor: Editor?) {
        val commentSaver = CommentSaver(element)
        val innerExpression = element.expression ?: return
        val replaced = element.replace(innerExpression)
        if (innerExpression.firstChild is KtLambdaExpression) {
            KtPsiFactory(element).appendSemicolonBeforeLambdaContainingElement(replaced)
        }
        commentSaver.restore(replaced)
    }
}
