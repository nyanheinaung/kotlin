/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.expression

import org.jetbrains.kotlin.js.backend.ast.JsBlock
import org.jetbrains.kotlin.js.backend.ast.JsTry
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.general.AbstractTranslator
import org.jetbrains.kotlin.js.translate.general.Translation.translateAsStatementAndMergeInBlockIfNeeded
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils.convertToBlock

class TryTranslator(
        val expression: KtTryExpression,
        context: TranslationContext
) : AbstractTranslator(context) {
    fun translate(): JsTry {
        val tryBlock = translateAsBlock(expression.tryBlock)

        val catchTranslator = CatchTranslator(expression.catchClauses, expression, context())
        val catchBlock = catchTranslator.translate()

        val finallyExpression = expression.finallyBlock?.finalExpression
        val finallyBlock = translateAsBlock(finallyExpression)

        return JsTry(tryBlock, catchBlock, finallyBlock)
    }

    private fun translateAsBlock(expression: KtExpression?): JsBlock? {
        if (expression == null) return null

        val statement = translateAsStatementAndMergeInBlockIfNeeded(expression, context())
        return convertToBlock(statement)
    }
}

