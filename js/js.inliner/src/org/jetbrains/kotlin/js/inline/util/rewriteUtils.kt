/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util

import org.jetbrains.kotlin.js.backend.ast.JsExpression
import org.jetbrains.kotlin.js.backend.ast.JsName
import org.jetbrains.kotlin.js.backend.ast.JsNode
import org.jetbrains.kotlin.js.inline.util.rewriters.NameReplacingVisitor
import org.jetbrains.kotlin.js.inline.util.rewriters.ThisReplacingVisitor

fun <T : JsNode> replaceNames(node: T, replaceMap: Map<JsName, JsExpression>): T {
    return if (replaceMap.isEmpty()) node else NameReplacingVisitor(replaceMap).accept(node)!!
}

fun <T : JsNode> replaceThisReference(node: T, replacement: JsExpression) {
    ThisReplacingVisitor(replacement).accept(node)
}
