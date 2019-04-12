/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.surroundWith.expression

import com.intellij.codeInsight.CodeInsightUtilBase
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.conversion.copy.range
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.utils.sure

class KotlinWithIfExpressionSurrounder(val withElse: Boolean) : KotlinExpressionSurrounder() {
    override fun isApplicable(expression: KtExpression) =
        super.isApplicable(expression) && (expression.analyze(BodyResolveMode.PARTIAL).getType(expression)?.isBoolean() ?: false)

    override fun surroundExpression(project: Project, editor: Editor, expression: KtExpression): TextRange? {
        val factory = KtPsiFactory(project)
        val replaceResult = expression.replace(
            factory.createIf(
                expression,
                factory.createBlock("blockStubContentToBeRemovedLater"),
                if (withElse) factory.createEmptyBody() else null
            )
        ) as KtExpression

        val ifExpression = KtPsiUtil.deparenthesizeOnce(replaceResult) as KtIfExpression

        CodeInsightUtilBase.forcePsiPostprocessAndRestoreElement(ifExpression)

        val firstStatementInThenRange = (ifExpression.then as? KtBlockExpression).sure {
            "Then branch should exist and be a block expression"
        }.statements.first().range

        editor.document.deleteString(firstStatementInThenRange.startOffset, firstStatementInThenRange.endOffset)

        return TextRange(firstStatementInThenRange.startOffset, firstStatementInThenRange.startOffset)
    }

    override fun getTemplateDescription() = "if (expr) { ... }" + (if (withElse) " else { ... }" else "")
}
