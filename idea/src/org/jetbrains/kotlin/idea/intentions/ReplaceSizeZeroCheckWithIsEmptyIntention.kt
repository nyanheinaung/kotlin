/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression

class ReplaceSizeZeroCheckWithIsEmptyInspection : IntentionBasedInspection<KtBinaryExpression>(ReplaceSizeZeroCheckWithIsEmptyIntention::class)

class ReplaceSizeZeroCheckWithIsEmptyIntention : ReplaceSizeCheckIntention("Replace size zero check with 'isEmpty'") {

    override fun getGenerateMethodSymbol() = "isEmpty()"

    override fun getTargetExpression(element: KtBinaryExpression): KtExpression? {
        return when (element.operationToken) {
            KtTokens.EQEQ -> when {
                element.right.isZero() -> element.left
                element.left.isZero() -> element.right
                else -> null
            }
            KtTokens.GT -> if (element.left.isOne()) element.right else null
            KtTokens.LT -> if (element.right.isOne()) element.left else null
            KtTokens.GTEQ -> if (element.left.isZero()) element.right else null
            KtTokens.LTEQ -> if (element.right.isZero()) element.left else null
            else -> null
        }
    }
}