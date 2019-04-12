/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import java.util.*

fun unrollToLeftMostQualifiedExpression(expression: KtQualifiedExpression): List<KtQualifiedExpression> {
    val unrolled = arrayListOf<KtQualifiedExpression>()

    var finger = expression
    while (true) {
        unrolled.add(finger)
        val receiver = finger.receiverExpression
        if (receiver !is KtQualifiedExpression) {
            break
        }
        finger = receiver
    }

    return unrolled.asReversed()
}

data class CallExpressionElement internal constructor(val qualified: KtQualifiedExpression) {

    val receiver: KtExpression
        get() = qualified.receiverExpression

    val selector: KtExpression?
        get() = qualified.selectorExpression

    val safe: Boolean
        get() = qualified.operationSign == KtTokens.SAFE_ACCESS

    val node: ASTNode
        get() = qualified.operationTokenNode
}