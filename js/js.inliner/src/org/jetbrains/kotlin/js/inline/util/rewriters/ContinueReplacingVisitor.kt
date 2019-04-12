/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util.rewriters

import org.jetbrains.kotlin.js.backend.ast.*

class ContinueReplacingVisitor(val loopLabelName: JsName?, val guardLabelName: JsName) : JsVisitorWithContextImpl() {
    var loopNestingLevel = 0

    override fun visit(x: JsFunction, ctx: JsContext<JsNode>) = false

    override fun visit(x: JsContinue, ctx: JsContext<JsNode>): Boolean {
        val target = x.label?.name
        val shouldReplace = if (target == null) loopNestingLevel == 0 else target == loopLabelName
        assert(loopNestingLevel >= 0)
        if (shouldReplace) {
            ctx.replaceMe(JsBreak(guardLabelName.makeRef()))
        }

        return false
    }

    override fun visit(x: JsLoop, ctx: JsContext<JsNode>): Boolean {
        if (loopLabelName == null) return false

        loopNestingLevel++
        return super.visit(x, ctx)
    }

    override fun endVisit(x: JsLoop, ctx: JsContext<JsNode>) {
        super.endVisit(x, ctx)
        if (loopLabelName == null) return
        loopNestingLevel--
    }
}
