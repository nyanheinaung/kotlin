/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.coroutine

import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.coroutineMetadata
import org.jetbrains.kotlin.js.backend.ast.metadata.isInlineableCoroutineBody
import org.jetbrains.kotlin.js.inline.ImportIntoFragmentInliningScope
import org.jetbrains.kotlin.js.translate.declaration.transformCoroutineMetadataToSpecialFunctions
import org.jetbrains.kotlin.js.translate.expression.InlineMetadata
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils

class CoroutineTransformer : JsVisitorWithContextImpl() {

    val functionName = mutableMapOf<JsFunction, String?>()

    override fun visit(x: JsExpressionStatement, ctx: JsContext<*>): Boolean {
        val expression = x.expression
        val assignment = JsAstUtils.decomposeAssignment(expression)
        if (assignment != null) {
            val (lhs, rhs) = assignment
            InlineMetadata.tryExtractFunction(rhs)?.let { wrapper ->
                val function = wrapper.function
                val name = ((lhs as? JsNameRef)?.name ?: function.name)?.ident
                functionName[function] = name
            }
        } else if (expression is JsFunction) {
            functionName[expression] = expression.name?.ident
        }
        return super.visit(x, ctx)
    }

    override fun visit(x: JsFunction, ctx: JsContext<*>): Boolean {
        if (x.isInlineableCoroutineBody) {
            x.body = transformCoroutineMetadataToSpecialFunctions(x.body)
            return false
        }
        if (x.coroutineMetadata != null) {
            lastStatementLevelContext.addPrevious(CoroutineFunctionTransformer(x, functionName[x]).transform())
            x.coroutineMetadata = null
            return false
        }
        return super.visit(x, ctx)
    }

    override fun visit(x: JsVars.JsVar, ctx: JsContext<*>): Boolean {
        val initExpression = x.initExpression
        if (initExpression != null) {
            InlineMetadata.tryExtractFunction(initExpression)?.let { wrapper ->
                functionName[wrapper.function] = x.name.ident
            }
        }
        return super.visit(x, ctx)
    }
}

fun transformCoroutines(fragments: Iterable<JsProgramFragment>) {
    val coroutineTransformer = CoroutineTransformer()
    for (fragment in fragments) {
        ImportIntoFragmentInliningScope.process(fragment) { scope ->
            coroutineTransformer.accept(scope.allCode)
        }
    }
}