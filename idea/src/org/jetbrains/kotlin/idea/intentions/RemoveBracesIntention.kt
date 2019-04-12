/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.*

class RemoveBracesIntention : SelfTargetingIntention<KtElement>(KtElement::class.java, "Remove braces") {

    private fun KtElement.findChildBlock() = when (this) {
        is KtBlockExpression -> this
        is KtLoopExpression -> body as? KtBlockExpression
        is KtWhenEntry -> expression as? KtBlockExpression
        else -> null
    }

    override fun isApplicableTo(element: KtElement, caretOffset: Int): Boolean {
        val block = element.findChildBlock() ?: return false
        val singleStatement = block.statements.singleOrNull() ?: return false
        when (val container = block.parent) {
            is KtContainerNode -> {
                if (singleStatement is KtIfExpression) {
                    val elseExpression = (container.parent as? KtIfExpression)?.`else`
                    if (elseExpression != null && elseExpression != block) return false
                }

                val description = container.description() ?: return false
                text = "Remove braces from '$description' statement"
                return true
            }
            is KtWhenEntry -> {
                text = "Remove braces from 'when' entry"
                return singleStatement !is KtNamedDeclaration
            }
            else -> return false
        }
    }

    override fun applyTo(element: KtElement, editor: Editor?) {
        val block = element.findChildBlock() ?: return
        val statement = block.statements.single()

        val container = block.parent
        val construct = container.parent as KtExpression
        handleComments(construct, block)

        val newElement = block.replace(statement.copy())

        val factory = KtPsiFactory(block)

        if (construct is KtDoWhileExpression) {
            newElement.parent!!.addAfter(factory.createNewLine(), newElement)
        }

        if (construct is KtIfExpression &&
            container.node.elementType == KtNodeTypes.ELSE &&
            construct.parent is KtExpression &&
            construct.parent !is KtStatementExpression
        ) {
            construct.replace(factory.createExpressionByPattern("($0)", construct))
        }
    }

    private fun handleComments(construct: KtExpression, block: KtBlockExpression) {
        var sibling = block.firstChild?.nextSibling

        while (sibling != null) {
            if (sibling is PsiComment) {
                //cleans up extra whitespace
                val psiFactory = KtPsiFactory(construct)
                if (construct.prevSibling is PsiWhiteSpace) {
                    construct.prevSibling!!.replace(psiFactory.createNewLine())
                }
                val commentElement = construct.parent.addBefore(sibling, construct.prevSibling)
                construct.parent.addBefore(psiFactory.createNewLine(), commentElement)
            }
            sibling = sibling.nextSibling
        }
    }

    override fun allowCaretInsideElement(element: PsiElement) = element !is KtBlockExpression || element.parent is KtWhenEntry
}
