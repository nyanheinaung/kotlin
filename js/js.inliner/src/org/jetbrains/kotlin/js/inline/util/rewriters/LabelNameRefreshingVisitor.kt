/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util.rewriters

import org.jetbrains.kotlin.js.backend.ast.*
import java.util.*

class LabelNameRefreshingVisitor(val functionScope: JsFunctionScope) : JsVisitorWithContextImpl() {
    private val substitutions: MutableMap<JsName, ArrayDeque<JsName>> = mutableMapOf()

    override fun visit(x: JsFunction, ctx: JsContext<JsNode>): Boolean = false

    override fun endVisit(x: JsBreak, ctx: JsContext<JsNode>) {
        val label = x.label?.name
        if (label != null) {
            ctx.replaceMe(JsBreak(getSubstitution(label).makeRef()).source(x.source))
        }
        super.endVisit(x, ctx)
    }

    override fun endVisit(x: JsContinue, ctx: JsContext<JsNode>) {
        val label = x.label?.name
        if (label != null) {
            ctx.replaceMe(JsContinue(getSubstitution(label).makeRef()).source(x.source))
        }
        super.endVisit(x, ctx)
    }

    override fun visit(x: JsLabel, ctx: JsContext<JsNode>): Boolean {
        val labelName = x.name
        val freshName = functionScope.enterLabel(labelName.ident, labelName.ident)
        substitutions.getOrPut(labelName) { ArrayDeque() }.push(freshName)

        return super.visit(x, ctx)
    }

    override fun endVisit(x: JsLabel, ctx: JsContext<JsNode>) {
        val labelName = x.name
        val stack = substitutions[labelName]!!
        val replacementLabel = JsLabel(stack.pop(), x.statement).apply { copyMetadataFrom(x) }
        ctx.replaceMe(replacementLabel)
        functionScope.exitLabel()
        super.endVisit(x, ctx)
    }

    private fun getSubstitution(name: JsName) = substitutions[name]?.peek() ?: name
}
