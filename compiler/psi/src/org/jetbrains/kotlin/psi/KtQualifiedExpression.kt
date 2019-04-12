/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import java.util.*

interface KtQualifiedExpression : KtExpression {
    val receiverExpression: KtExpression
        get() = getExpression(false) ?: throw AssertionError("No receiver found: ${getElementTextWithContext()}")

    val selectorExpression: KtExpression?
        get() = getExpression(true)

    val operationTokenNode: ASTNode
        get() = node.findChildByType(KtTokens.OPERATIONS) ?: error(
            "No operation node for ${node.elementType}. Children: ${Arrays.toString(children)}"
        )

    val operationSign: KtSingleValueToken
        get() = operationTokenNode.elementType as KtSingleValueToken

    private fun KtQualifiedExpression.getExpression(afterOperation: Boolean): KtExpression? {
        return operationTokenNode.psi?.siblings(afterOperation, false)?.firstIsInstanceOrNull<KtExpression>()
    }
}
