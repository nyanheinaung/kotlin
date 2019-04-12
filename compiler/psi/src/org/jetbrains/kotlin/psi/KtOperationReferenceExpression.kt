/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.parsing.KotlinExpressionParsing
import org.jetbrains.kotlin.types.expressions.OperatorConventions

class KtOperationReferenceExpression(node: ASTNode) : KtSimpleNameExpressionImpl(node) {
    override fun getReferencedNameElement() = findChildByType<PsiElement?>(KotlinExpressionParsing.ALL_OPERATIONS) ?: this

    val operationSignTokenType: KtSingleValueToken?
        get() = (firstChild as? TreeElement)?.elementType as? KtSingleValueToken

    fun isConventionOperator(): Boolean {
        val tokenType = operationSignTokenType ?: return false
        return OperatorConventions.getNameForOperationSymbol(tokenType) != null
    }
}
