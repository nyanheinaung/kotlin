/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.intentions.callExpression
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getExplicitReceiverValue
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf

class ReplacePutWithAssignmentInspection : AbstractApplicabilityBasedInspection<KtDotQualifiedExpression>(
    KtDotQualifiedExpression::class.java
) {

    override fun isApplicable(element: KtDotQualifiedExpression): Boolean {
        if (element.receiverExpression is KtSuperExpression) return false

        val callExpression = element.callExpression
        if (callExpression?.valueArguments?.size != 2) return false

        val calleeExpression = callExpression.calleeExpression as? KtNameReferenceExpression ?: return false
        if (calleeExpression.getReferencedName() !in compatibleNames) return false

        val context = element.analyze()
        if (element.isUsedAsExpression(context)) return false

        // This fragment had to be added because of incorrect behaviour of isUsesAsExpression
        // TODO: remove it after fix of KT-25682
        val binaryExpression = element.getStrictParentOfType<KtBinaryExpression>()
        val right = binaryExpression?.right
        if (binaryExpression?.operationToken == KtTokens.ELVIS &&
            right != null && (right == element || KtPsiUtil.deparenthesize(right) == element)
        ) return false

        val resolvedCall = element.getResolvedCall(context)
        val receiverType = resolvedCall?.getExplicitReceiverValue()?.type ?: return false
        val receiverClass = receiverType.constructor.declarationDescriptor as? ClassDescriptor ?: return false
        return receiverClass.isSubclassOf(DefaultBuiltIns.Instance.mutableMap)
    }

    override fun applyTo(element: PsiElement, project: Project, editor: Editor?) {
        val expression = element.getParentOfType<KtDotQualifiedExpression>(strict = false) ?: return
        val valueArguments = expression.callExpression?.valueArguments ?: return
        val firstArg = valueArguments[0]?.getArgumentExpression() ?: return
        val secondArg = valueArguments[1]?.getArgumentExpression() ?: return
        val label = if (secondArg is KtLambdaExpression) {
            val returnLabel = secondArg.findDescendantOfType<KtReturnExpression>()?.getLabelName()
            compatibleNames.firstOrNull { it == returnLabel }?.plus("@") ?: ""
        } else ""
        expression.replace(
            KtPsiFactory(expression).createExpressionByPattern(
                "$0[$1] = $label$2",
                expression.receiverExpression,
                firstArg,
                secondArg
            )
        )
    }

    override fun inspectionTarget(element: KtDotQualifiedExpression) = element.callExpression?.calleeExpression ?: element

    override fun inspectionText(element: KtDotQualifiedExpression): String = "map.put() should be converted to assignment"

    override val defaultFixText = "Convert put to assignment"

    companion object {
        private val compatibleNames = setOf("put")
    }
}