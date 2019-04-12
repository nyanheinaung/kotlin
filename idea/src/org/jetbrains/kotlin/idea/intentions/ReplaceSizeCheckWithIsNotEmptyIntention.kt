/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions

import org.jetbrains.kotlin.idea.inspections.IntentionBasedInspection
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression

class ReplaceSizeCheckWithIsNotEmptyInspection : IntentionBasedInspection<KtBinaryExpression>(ReplaceSizeCheckWithIsNotEmptyIntention::class)

class ReplaceSizeCheckWithIsNotEmptyIntention : ReplaceSizeCheckIntention("Replace size check with 'isNotEmpty'") {

    override fun getGenerateMethodSymbol() = "isNotEmpty()"

    override fun getTargetExpression(element: KtBinaryExpression): KtExpression? {
        return when (element.operationToken) {
            KtTokens.EXCLEQ -> when {
                element.right.isZero() -> element.left
                element.left.isZero() -> element.right
                else -> null
            }
            KtTokens.GT -> if (element.right.isZero()) element.left else null
            KtTokens.LT -> if (element.left.isZero()) element.right else null
            KtTokens.GTEQ -> if (element.right.isOne()) element.left else null
            KtTokens.LTEQ -> if (element.left.isOne()) element.right else null
            else -> null
        }
    }
}