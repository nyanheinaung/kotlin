/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiUtil
import java.util.*

interface UnreachableCode {
    val elements: Set<KtElement>
    fun getUnreachableTextRanges(element: KtElement): List<TextRange>
}

class UnreachableCodeImpl(
    private val reachableElements: Set<KtElement>,
    private val unreachableElements: Set<KtElement>
) : UnreachableCode {

    // This is needed in order to highlight only '1 < 2' and not '1', '<' and '2' as well
    override val elements: Set<KtElement> = KtPsiUtil.findRootExpressions(unreachableElements)

    override fun getUnreachableTextRanges(element: KtElement): List<TextRange> {
        return if (element.hasChildrenInSet(reachableElements)) {
            with(element.getLeavesOrReachableChildren().removeReachableElementsWithMeaninglessSiblings().mergeAdjacentTextRanges()) {
                if (isNotEmpty()) this
                // Specific case like condition in when:
                // element is dead but its only child is alive and has the same text range
                else listOf(element.textRange.endOffset.let { TextRange(it, it) })
            }
        } else {
            listOf(element.textRange!!)
        }
    }

    private fun KtElement.hasChildrenInSet(set: Set<KtElement>): Boolean =
        PsiTreeUtil.collectElements(this) { it != this }.any { it in set }

    private fun KtElement.getLeavesOrReachableChildren(): List<PsiElement> {
        val children = ArrayList<PsiElement>()
        acceptChildren(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                val isReachable =
                    element is KtElement && reachableElements.contains(element) && !element.hasChildrenInSet(unreachableElements)
                if (isReachable || element.children.isEmpty()) {
                    children.add(element)
                } else {
                    element.acceptChildren(this)
                }
            }
        })
        return children
    }

    private fun List<PsiElement>.removeReachableElementsWithMeaninglessSiblings(): List<PsiElement> {
        fun PsiElement.isMeaningless() = this is PsiWhiteSpace
                || this.node?.elementType == KtTokens.COMMA
                || this is PsiComment

        val childrenToRemove = HashSet<PsiElement>()
        fun collectSiblingsIfMeaningless(elementIndex: Int, direction: Int) {
            val index = elementIndex + direction
            if (index !in 0..(size - 1)) return

            val element = this[index]
            if (element.isMeaningless()) {
                childrenToRemove.add(element)
                collectSiblingsIfMeaningless(index, direction)
            }
        }
        for ((index, element) in this.withIndex()) {
            if (reachableElements.contains(element)) {
                childrenToRemove.add(element)
                collectSiblingsIfMeaningless(index, -1)
                collectSiblingsIfMeaningless(index, 1)
            }
        }
        return this.filter { it !in childrenToRemove }
    }


    private fun List<PsiElement>.mergeAdjacentTextRanges(): List<TextRange> {
        val result = ArrayList<TextRange>()
        val lastRange = fold(null as TextRange?) { currentTextRange, element ->

            val elementRange = element.textRange!!
            when {
                currentTextRange == null -> {
                    elementRange
                }
                currentTextRange.endOffset == elementRange.startOffset -> {
                    currentTextRange.union(elementRange)
                }
                else -> {
                    result.add(currentTextRange)
                    elementRange
                }
            }
        }
        if (lastRange != null) {
            result.add(lastRange)
        }
        return result
    }
}
