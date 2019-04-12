/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.clean

import org.jetbrains.kotlin.js.backend.ast.JsContext
import org.jetbrains.kotlin.js.backend.ast.JsNode
import org.jetbrains.kotlin.js.backend.ast.JsVisitorWithContextImpl

internal class NodeRemover<T>(val klass: Class<T>, val predicate: (T) -> Boolean): JsVisitorWithContextImpl() {

    override fun <T : JsNode> doTraverse(node: T, ctx: JsContext<*>) {
        if (klass.isInstance(node)) {
            val instance = klass.cast(node)!!

            if (predicate(instance)) {
                ctx.removeMe()
                return
            }
        }

        super.doTraverse(node, ctx)
    }
}
