/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.intentions.resolvedToArrayType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.createExpressionByPattern
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

class ReplaceArrayEqualityOpWithArraysEqualsInspection : AbstractApplicabilityBasedInspection<KtBinaryExpression>(
        KtBinaryExpression::class.java
) {
    override fun applyTo(element: PsiElement, project: Project, editor: Editor?) {
        val expression = element as? KtBinaryExpression ?: return
        val right = expression.right ?: return
        val left = expression.left ?: return
        val factory = KtPsiFactory(project)
        val template = buildString {
            if (expression.operationToken == KtTokens.EXCLEQ) append("!")
            append("$0.contentEquals($1)")
        }
        expression.replace(factory.createExpressionByPattern(template, left, right))
    }

    override fun isApplicable(element: KtBinaryExpression): Boolean {
        val operationToken = element.operationToken
        when (operationToken) {
            KtTokens.EQEQ, KtTokens.EXCLEQ -> {}
            else -> return false
        }
        val right = element.right
        val left = element.left
        if (right == null || left == null) return false
        val context = element.analyze()
        val rightResolvedCall = right.getResolvedCall(context)
        val leftResolvedCall = left.getResolvedCall(context)
        return rightResolvedCall?.resolvedToArrayType() == true && leftResolvedCall?.resolvedToArrayType() == true
    }

    override fun inspectionText(element: KtBinaryExpression) = "Dangerous array comparison"

    override val defaultFixText: String
        get() = "Replace with 'contentEquals'"

    override fun fixText(element: KtBinaryExpression): String = when (element.operationToken) {
        KtTokens.EQEQ -> "Replace '==' with 'contentEquals'"
        KtTokens.EXCLEQ -> "Replace '!=' with 'contentEquals'"
        else -> ""
    }
}
