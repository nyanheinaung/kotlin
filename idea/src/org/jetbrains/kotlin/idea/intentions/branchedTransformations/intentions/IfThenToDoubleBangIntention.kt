/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions.branchedTransformations.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.intentions.SelfTargetingRangeIntention
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.*
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsStatement

class IfThenToDoubleBangIntention : SelfTargetingRangeIntention<KtIfExpression>(
        KtIfExpression::class.java, "Replace 'if' expression with '!!' expression"
) {
    override fun applicabilityRange(element: KtIfExpression): TextRange? {
        val (context, condition, receiverExpression, baseClause, negatedClause) = element.buildSelectTransformationData() ?: return null
        // TODO: here "Replace with as" can be supported
        if (condition is KtIsExpression) return null

        val throwExpression = negatedClause as? KtThrowExpression ?: return null

        val matchesAsStatement = element.isUsedAsStatement(context) && (baseClause?.isNullExpressionOrEmptyBlock() ?: true)
        if (!matchesAsStatement &&
            !(baseClause?.evaluatesTo(receiverExpression) ?: false && receiverExpression.isStableSimpleExpression())) return null

        var text = "Replace 'if' expression with '!!' expression"
        if (!throwExpression.throwsNullPointerExceptionWithNoArguments()) {
            text += " (will remove exception)"
        }

        setText(text)
        val rParen = element.rightParenthesis ?: return null
        return TextRange(element.startOffset, rParen.endOffset)
    }

    override fun startInWriteAction() = false

    override fun applyTo(element: KtIfExpression, editor: Editor?) {
        val (_, _, receiverExpression) = element.buildSelectTransformationData() ?: return
        val result = runWriteAction {
            element.replace(KtPsiFactory(element).createExpressionByPattern("$0!!", receiverExpression)) as KtPostfixExpression
        }

        if (editor != null) {
            result.inlineBaseExpressionIfApplicableWithPrompt(editor)
        }
    }
}
