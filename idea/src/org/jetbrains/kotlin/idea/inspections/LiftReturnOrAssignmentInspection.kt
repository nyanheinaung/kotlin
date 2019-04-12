/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInspection.*
import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING
import com.intellij.codeInspection.ProblemHighlightType.INFORMATION
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.BranchedFoldingUtils
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.isElseIf
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.lineCount
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class LiftReturnOrAssignmentInspection : AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession) =
        object : KtVisitorVoid() {
            private fun visitIfOrWhenOrTry(expression: KtExpression, keyword: PsiElement) {
                if (expression.lineCount() > LINES_LIMIT) return
                if (expression.isElseIf()) return

                val foldableReturns = BranchedFoldingUtils.getFoldableReturns(expression)
                if (foldableReturns?.isNotEmpty() == true) {
                    val hasOtherReturns = expression.anyDescendantOfType<KtReturnExpression> { it !in foldableReturns }
                    val isSerious = !hasOtherReturns && foldableReturns.size > 1
                    registerProblem(expression, keyword, isSerious, LiftReturnOutFix(keyword.text))
                    foldableReturns.forEach {
                        registerProblem(expression, keyword, isSerious, LiftReturnOutFix(keyword.text), it, INFORMATION)
                    }
                    return
                }

                val assignmentNumber = BranchedFoldingUtils.getFoldableAssignmentNumber(expression)
                if (assignmentNumber > 0) {
                    val isSerious = assignmentNumber > 1
                    registerProblem(expression, keyword, isSerious, LiftAssignmentOutFix(keyword.text))
                }
            }

            private fun registerProblem(
                expression: KtExpression,
                keyword: PsiElement,
                isSerious: Boolean,
                fix: LocalQuickFix,
                highlightElement: PsiElement = keyword,
                highlightType: ProblemHighlightType = if (isSerious) GENERIC_ERROR_OR_WARNING else INFORMATION
            ) {
                val subject = if (fix is LiftReturnOutFix) "Return" else "Assignment"
                val verb = if (isSerious) "should" else "can"
                holder.registerProblemWithoutOfflineInformation(
                    expression,
                    "$subject $verb be lifted out of '${keyword.text}'",
                    isOnTheFly,
                    highlightType,
                    highlightElement.textRange?.shiftRight(-expression.startOffset),
                    fix
                )
            }

            override fun visitIfExpression(expression: KtIfExpression) {
                super.visitIfExpression(expression)
                visitIfOrWhenOrTry(expression, expression.ifKeyword)
            }

            override fun visitWhenExpression(expression: KtWhenExpression) {
                super.visitWhenExpression(expression)
                visitIfOrWhenOrTry(expression, expression.whenKeyword)
            }

            override fun visitTryExpression(expression: KtTryExpression) {
                super.visitTryExpression(expression)
                expression.tryKeyword?.let {
                    visitIfOrWhenOrTry(expression, it)
                }
            }
        }

    private class LiftReturnOutFix(private val keyword: String) : LocalQuickFix {
        override fun getName() = "Lift return out of '$keyword'"

        override fun getFamilyName() = name

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val replaced = BranchedFoldingUtils.foldToReturn(descriptor.psiElement as KtExpression)
            replaced.findExistingEditor()?.caretModel?.moveToOffset(replaced.startOffset)
        }
    }

    private class LiftAssignmentOutFix(private val keyword: String) : LocalQuickFix {
        override fun getName() = "Lift assignment out of '$keyword'"

        override fun getFamilyName() = name

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            BranchedFoldingUtils.foldToAssignment(descriptor.psiElement as KtExpression)
        }
    }

    companion object {
        private const val LINES_LIMIT = 15
    }
}
