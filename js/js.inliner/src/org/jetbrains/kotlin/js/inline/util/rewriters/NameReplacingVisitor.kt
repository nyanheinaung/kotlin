/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util.rewriters

import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.coroutineMetadata

class NameReplacingVisitor(private val replaceMap: Map<JsName, JsExpression>) : JsVisitorWithContextImpl() {

    override fun endVisit(x: JsNameRef, ctx: JsContext<JsNode>) {
        if (x.qualifier != null) return
        val replacement = replaceMap[x.name] ?: return
        if (replacement is JsNameRef) {
            applyToNamedNode(x)
        }
        else {
            val replacementCopy = replacement.deepCopy()
            if (x.source != null) {
                replacementCopy.source = x.source
            }
            ctx.replaceMe(accept(replacementCopy))
        }
    }

    override fun endVisit(x: JsVars.JsVar, ctx: JsContext<*>) = applyToNamedNode(x)

    override fun endVisit(x: JsLabel, ctx: JsContext<*>) = applyToNamedNode(x)

    override fun endVisit(x: JsFunction, ctx: JsContext<*>) = applyToNamedNode(x)

    override fun endVisit(x: JsParameter, ctx: JsContext<*>) = applyToNamedNode(x)

    override fun visit(x: JsFunction, ctx: JsContext<*>): Boolean {
        x.coroutineMetadata?.let { coroutineMetadata ->
            x.coroutineMetadata = coroutineMetadata.copy(
                baseClassRef = accept(coroutineMetadata.baseClassRef.deepCopy()),
                suspendObjectRef = accept(coroutineMetadata.suspendObjectRef.deepCopy())
            )
        }
        return super.visit(x, ctx)
    }

    private fun applyToNamedNode(x: HasName) {
        while (true) {
            val replacement = replaceMap[x.name]
            if (replacement is HasName) {
                x.name = replacement.name
            }
            else {
                break
            }
        }
    }
}
