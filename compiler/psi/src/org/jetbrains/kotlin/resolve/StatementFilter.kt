/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

open class StatementFilter {
    open val filter: ((KtExpression) -> Boolean)?
        get() = null

    companion object {
        @JvmField
        val NONE = object : StatementFilter() {
            override fun toString() = "NONE"
        }
    }
}

fun StatementFilter.filterStatements(block: KtBlockExpression): List<KtExpression> {
    if (filter == null || block is KtPsiUtil.KtExpressionWrapper) return block.statements
    return block.statements.filter { filter!!(it) }
}

fun StatementFilter.getLastStatementInABlock(block: KtBlockExpression) = filterStatements(block).lastOrNull()