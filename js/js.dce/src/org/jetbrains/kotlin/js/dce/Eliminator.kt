/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.dce

import org.jetbrains.kotlin.js.backend.ast.*

class Eliminator(private val analysisResult: AnalysisResult) : JsVisitorWithContextImpl() {
    override fun visit(x: JsVars.JsVar, ctx: JsContext<*>): Boolean = removeIfNecessary(x, ctx)

    override fun visit(x: JsExpressionStatement, ctx: JsContext<*>): Boolean = removeIfNecessary(x, ctx)

    override fun visit(x: JsReturn, ctx: JsContext<*>): Boolean = removeIfNecessary(x, ctx)

    private fun removeIfNecessary(x: JsNode, ctx: JsContext<*>): Boolean {
        if (x in analysisResult.astNodesToEliminate) {
            ctx.removeMe()
            return false
        }
        val node = analysisResult.nodeMap[x]?.original
        return if (!isUsed(node)) {
            ctx.removeMe()
            false
        }
        else {
            true
        }
    }

    override fun endVisit(x: JsVars, ctx: JsContext<*>) {
        if (x.vars.isEmpty()) {
            ctx.removeMe()
        }
    }

    private fun isUsed(node: Context.Node?): Boolean = node == null || node.declarationReachable
}