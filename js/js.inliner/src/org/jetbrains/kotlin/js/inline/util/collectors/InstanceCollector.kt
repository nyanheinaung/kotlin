/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util.collectors

import org.jetbrains.kotlin.js.backend.ast.JsFunction
import org.jetbrains.kotlin.js.backend.ast.JsNode
import org.jetbrains.kotlin.js.backend.ast.JsObjectLiteral
import org.jetbrains.kotlin.js.backend.ast.RecursiveJsVisitor
import java.util.ArrayList

class InstanceCollector<T : JsNode>(val klass: Class<T>, val visitNestedDeclarations: Boolean) : RecursiveJsVisitor() {
    val collected: MutableList<T> = ArrayList()

    override fun visitFunction(x: JsFunction) {
        if (visitNestedDeclarations) {
            visitElement(x)
        }
    }

    override fun visitObjectLiteral(x: JsObjectLiteral) {
        if (visitNestedDeclarations) {
            visitElement(x)
        }
    }

    override fun visitElement(node: JsNode) {
        if (klass.isInstance(node)) {
            collected.add(klass.cast(node)!!)
        }

        super.visitElement(node)
    }
}
