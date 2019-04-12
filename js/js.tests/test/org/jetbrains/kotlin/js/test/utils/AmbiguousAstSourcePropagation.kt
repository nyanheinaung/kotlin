/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.utils

import org.jetbrains.kotlin.js.backend.ast.*

class AmbiguousAstSourcePropagation : RecursiveJsVisitor() {
    private var lastSource: Any? = null

    override fun visitElement(node: JsNode) {
        val source = node.source
        if (source == null && node is JsExpression) {
            node.source = lastSource
        }

        val oldLastSource = lastSource
        lastSource = node.source
        super.visitElement(node)
        lastSource = oldLastSource
    }
}