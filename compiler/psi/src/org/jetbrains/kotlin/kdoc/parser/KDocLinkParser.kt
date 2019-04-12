/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KotlinLexer
import org.jetbrains.kotlin.lexer.KtTokens

/**
 * Parses the contents of a Markdown link in KDoc. Uses the standard Kotlin lexer.
 */
class KDocLinkParser : PsiParser {
    companion object {
        @JvmStatic
        fun parseMarkdownLink(root: IElementType, chameleon: ASTNode): ASTNode {
            val parentElement = chameleon.treeParent.psi
            val project = parentElement.project
            val builder = PsiBuilderFactory.getInstance().createBuilder(
                project,
                chameleon,
                KotlinLexer(),
                root.language,
                chameleon.text
            )
            val parser = KDocLinkParser()

            return parser.parse(root, builder).firstChildNode
        }
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        val hasLBracket = builder.tokenType == KtTokens.LBRACKET
        if (hasLBracket) {
            builder.advanceLexer()
        }
        parseQualifiedName(builder)
        if (hasLBracket) {
            if (!builder.eof() && builder.tokenType != KtTokens.RBRACKET) {
                builder.error("Closing bracket expected")
                while (!builder.eof() && builder.tokenType != KtTokens.RBRACKET) {
                    builder.advanceLexer()
                }
            }
            if (builder.tokenType == KtTokens.RBRACKET) {
                builder.advanceLexer()
            }
        } else {
            if (!builder.eof()) {
                builder.error("Expression expected")
                while (!builder.eof()) {
                    builder.advanceLexer()
                }
            }
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseQualifiedName(builder: PsiBuilder) {
        var marker = builder.mark()
        while (true) {
            // don't highlight a word in a link as an error if it happens to be a Kotlin keyword
            if (!isName(builder.tokenType)) {
                marker.drop()
                builder.error("Identifier expected")
                break
            }
            builder.advanceLexer()
            marker.done(KDocElementTypes.KDOC_NAME)
            if (builder.tokenType != KtTokens.DOT) {
                break
            }
            marker = marker.precede()
            builder.advanceLexer()
        }
    }

    private fun isName(tokenType: IElementType?) = tokenType == KtTokens.IDENTIFIER || tokenType in KtTokens.KEYWORDS
}
