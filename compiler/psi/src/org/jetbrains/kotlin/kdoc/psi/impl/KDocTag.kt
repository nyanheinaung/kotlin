/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import org.jetbrains.kotlin.kdoc.lexer.KDocTokens
import org.jetbrains.kotlin.kdoc.parser.KDocElementTypes
import org.jetbrains.kotlin.kdoc.parser.KDocKnownTag

open class KDocTag(node: ASTNode) : KDocElementImpl(node) {

    /**
     * Returns the name of this tag, not including the leading @ character.
     *
     * @return tag name or null if this tag represents the default section of a doc comment
     * or the code has a syntax error.
     */
    override fun getName(): String? {
        val tagName: PsiElement? = findChildByType(KDocTokens.TAG_NAME)
        if (tagName != null) {
            return tagName.text.substring(1)
        }
        return null
    }

    /**
     * Returns the name of the entity documented by this tag (for example, the name of the parameter
     * for the @param tag), or null if this tag does not document any specific entity.
     */
    open fun getSubjectName(): String? = getSubjectLink()?.getLinkText()

    fun getSubjectLink(): KDocLink? {
        val children = childrenAfterTagName()
        if (hasSubject(children)) {
            return children.firstOrNull()?.psi as? KDocLink
        }
        return null
    }

    val knownTag: KDocKnownTag?
        get() {
            return name?.let { KDocKnownTag.findByTagName(it) }
        }

    private fun hasSubject(contentChildren: List<ASTNode>): Boolean {
        if (knownTag?.isReferenceRequired ?: false) {
            return contentChildren.firstOrNull()?.elementType == KDocTokens.MARKDOWN_LINK
        }
        return false
    }

    private fun childrenAfterTagName(): List<ASTNode> =
        node.getChildren(null)
            .dropWhile { it.elementType == KDocTokens.TAG_NAME }
            .dropWhile { it.elementType == TokenType.WHITE_SPACE }

    /**
     * Returns the content of this tag (all text following the tag name and the subject if present,
     * with leading asterisks removed).
     */
    open fun getContent(): String {
        val builder = StringBuilder()
        val codeBlockBuilder = StringBuilder()
        var targetBuilder = builder

        var contentStarted = false
        var afterAsterisk = false
        var indentedCodeBlock = false

        fun isCodeBlock() = targetBuilder == codeBlockBuilder

        fun startCodeBlock() {
            targetBuilder = codeBlockBuilder
        }

        fun flushCodeBlock() {
            if (isCodeBlock()) {
                builder.append(trimCommonIndent(codeBlockBuilder, indentedCodeBlock))
                codeBlockBuilder.setLength(0)
                targetBuilder = builder
            }
        }

        var children = childrenAfterTagName()
        if (hasSubject(children)) {
            children = children.drop(1)
        }
        for (node in children) {
            val type = node.elementType
            if (type == KDocTokens.CODE_BLOCK_TEXT) {
                //If first line of code block
                if (!isCodeBlock())
                    indentedCodeBlock = indentedCodeBlock || node.text.startsWith(indentationWhiteSpaces) || node.text.startsWith("\t")
                startCodeBlock()
            } else if (KDocTokens.CONTENT_TOKENS.contains(type)) {
                flushCodeBlock()
                indentedCodeBlock = false
            }

            if (KDocTokens.CONTENT_TOKENS.contains(type)) {
                val isPlainContent = afterAsterisk && !isCodeBlock()
                // If content not yet started and not part of indented code block
                // and not inside fenced code block we should trim leading spaces
                val trimLeadingSpaces = !(contentStarted || indentedCodeBlock) || isPlainContent

                targetBuilder.append(if (trimLeadingSpaces) node.text.trimStart() else node.text)
                contentStarted = true
                afterAsterisk = false
            }
            if (type == KDocTokens.LEADING_ASTERISK) {
                afterAsterisk = true
            }
            if (type == TokenType.WHITE_SPACE && contentStarted) {
                targetBuilder.append("\n".repeat(StringUtil.countNewLines(node.text)))
            }
            if (type == KDocElementTypes.KDOC_TAG) {
                break
            }
        }

        flushCodeBlock()

        return builder.toString().trimEnd(' ', '\t')
    }

    private fun trimCommonIndent(builder: StringBuilder, prepend4WhiteSpaces: Boolean = false): String {
        val lines = builder.toString().split('\n')
        val minIndent = lines.filter { it.trim().isNotEmpty() }.map { it.calcIndent() }.min() ?: 0
        var processedLines = lines.map { it.drop(minIndent) }
        if (prepend4WhiteSpaces)
            processedLines = processedLines.map { if (it.isNotBlank()) it.prependIndent(indentationWhiteSpaces) else it }
        return processedLines.joinToString("\n")
    }

    private fun String.calcIndent() = indexOfFirst { !it.isWhitespace() }

    companion object {
        val indentationWhiteSpaces = " ".repeat(4)
    }
}
