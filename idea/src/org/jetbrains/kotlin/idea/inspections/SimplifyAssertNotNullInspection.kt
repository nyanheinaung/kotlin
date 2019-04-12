/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import com.intellij.codeInsight.CodeInsightUtilCore
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.idea.intentions.branchedTransformations.expressionComparedToNull
import org.jetbrains.kotlin.idea.util.CommentSaver
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.calls.model.isReallySuccess
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

class SimplifyAssertNotNullInspection : AbstractApplicabilityBasedInspection<KtCallExpression>(KtCallExpression::class.java) {

    override fun isApplicable(element: KtCallExpression): Boolean {
        if ((element.calleeExpression as? KtNameReferenceExpression)?.getReferencedName() != "assert") return false

        val arguments = element.valueArguments
        if (arguments.size != 1 && arguments.size != 2) return false

        val condition = arguments.first().getArgumentExpression() as? KtBinaryExpression ?: return false
        if (condition.operationToken != KtTokens.EXCLEQ) return false
        val value = condition.expressionComparedToNull() as? KtNameReferenceExpression ?: return false

        val prevDeclaration = findVariableDeclaration(element) ?: return false
        if (value.getReferencedNameAsName() != prevDeclaration.nameAsName) return false
        if (prevDeclaration.initializer == null) return false

        val resolvedCall = element.resolveToCall() ?: return false
        if (!resolvedCall.isReallySuccess()) return false
        val function = resolvedCall.resultingDescriptor as? FunctionDescriptor ?: return false
        if (function.importableFqName?.asString() != "kotlin.assert") return false

        if (arguments.size != 1) {
            if (extractMessage(element) == null) return false
        }
        return true
    }

    override fun inspectionText(element: KtCallExpression) = "assert should be replaced with operator"

    override val defaultFixText: String = "Replace assert with operator"

    override fun fixText(element: KtCallExpression): String {
        return if (element.valueArguments.size == 1) {
            "Replace with '!!' operator"
        }
        else {
            "Replace with '?: error(...)'"
        }
    }

    override fun applyTo(element: PsiElement, project: Project, editor: Editor?) {
        val expression = element as? KtCallExpression ?: return
        val declaration = findVariableDeclaration(expression)!!
        val initializer = declaration.initializer!!
        val message = extractMessage(expression)

        val commentSaver = CommentSaver(expression)

        if (message == null) {
            val newInitializer = KtPsiFactory(expression).createExpressionByPattern("$0!!", initializer)
            initializer.replace(newInitializer)
        }
        else {
            val newInitializer = KtPsiFactory(expression).createExpressionByPattern("$0 ?: kotlin.error($1)", initializer, message)
            val result = initializer.replace(newInitializer)

            val qualifiedExpression = (result as KtBinaryExpression).right as KtDotQualifiedExpression
            ShortenReferences.DEFAULT.process(expression.containingKtFile,
                                              qualifiedExpression.startOffset,
                                              (qualifiedExpression.selectorExpression as KtCallExpression).calleeExpression!!.endOffset)
        }

        expression.delete()

        commentSaver.restore(declaration)

        if (editor != null) {
            val newInitializer = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(declaration).initializer!!
            val offset = if (message == null)
                newInitializer.endOffset
            else
                (newInitializer as KtBinaryExpression).operationReference.startOffset
            editor.moveCaret(offset)
        }
    }

    private fun findVariableDeclaration(element: KtCallExpression): KtVariableDeclaration? {
        if (element.parent !is KtBlockExpression) return null
        return element.siblings(forward = false, withItself = false).firstIsInstanceOrNull<KtExpression>() as? KtVariableDeclaration
    }

    private fun extractMessage(element: KtCallExpression): KtExpression? {
        val arguments = element.valueArguments
        if (arguments.size != 2) return null
        return (arguments[1].getArgumentExpression() as? KtLambdaExpression)
                              ?.bodyExpression
                              ?.statements
                              ?.singleOrNull()
    }
}
