/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.psi.impl

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtElementImpl
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

/**
 * A single part of a qualified name in the tag subject or link.
 */
class KDocName(node: ASTNode) : KtElementImpl(node) {
    fun getContainingDoc(): KDoc {
        val kdoc = getStrictParentOfType<KDoc>()
        return kdoc ?: throw IllegalStateException("KDocName must be inside a KDoc")
    }

    fun getContainingSection(): KDocSection {
        val kdoc = getStrictParentOfType<KDocSection>()
        return kdoc ?: throw IllegalStateException("KDocName must be inside a KDocSection")
    }

    fun getQualifier(): KDocName? = getChildOfType()

    /**
     * Returns the range within the element containing the name (in other words,
     * the range of the element excluding the qualifier and dot, if present).
     */
    fun getNameTextRange(): TextRange {
        val dot = node.findChildByType(KtTokens.DOT)
        val textRange = textRange
        val nameStart = if (dot != null) dot.textRange.endOffset - textRange.startOffset else 0
        return TextRange(nameStart, textRange.length)
    }

    fun getNameText(): String = getNameTextRange().substring(text)

    fun getQualifiedName(): List<String> {
        val qualifier = getQualifier()
        val nameAsList = listOf(getNameText())
        return if (qualifier != null) qualifier.getQualifiedName() + nameAsList else nameAsList
    }
}
