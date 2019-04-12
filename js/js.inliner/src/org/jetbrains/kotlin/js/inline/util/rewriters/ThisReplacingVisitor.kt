/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util.rewriters

import org.jetbrains.kotlin.js.backend.ast.*

class ThisReplacingVisitor(private val thisReplacement: JsExpression) : JsVisitorWithContextImpl() {
    override fun endVisit(x: JsThisRef, ctx: JsContext<JsNode>) {
        ctx.replaceMe(thisReplacement)
    }

    override fun visit(x: JsFunction, ctx: JsContext<JsNode>) = false

    override fun visit(x: JsObjectLiteral, ctx: JsContext<JsNode>) = false
}
