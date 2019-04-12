/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.dce

import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.SpecialFunction
import org.jetbrains.kotlin.js.backend.ast.metadata.specialFunction
import org.jetbrains.kotlin.js.dce.Context.Node

fun Context.isObjectDefineProperty(function: JsExpression) = isObjectFunction(function, "defineProperty")

fun Context.isObjectGetOwnPropertyDescriptor(function: JsExpression) = isObjectFunction(function, "getOwnPropertyDescriptor")

fun Context.isDefineModule(function: JsExpression): Boolean = isKotlinFunction(function, "defineModule")

fun Context.isDefineInlineFunction(function: JsExpression): Boolean =
        isKotlinFunction(function, "defineInlineFunction") || isSpecialFunction(function, SpecialFunction.DEFINE_INLINE_FUNCTION)

fun Context.isWrapFunction(function: JsExpression): Boolean =
        isKotlinFunction(function, "wrapFunction") || isSpecialFunction(function, SpecialFunction.WRAP_FUNCTION)

fun Context.isObjectFunction(function: JsExpression, functionName: String): Boolean {
    if (function !is JsNameRef) return false
    if (function.ident != functionName) return false

    val receiver = function.qualifier as? JsNameRef ?: return false
    if (receiver.name?.let { nodes[it] } != null) return false

    return receiver.ident == "Object"
}

fun Context.isKotlinFunction(function: JsExpression, name: String): Boolean {
    if (function !is JsNameRef || function.ident != name) return false
    val receiver = (function.qualifier as? JsNameRef)?.name ?: return false
    return receiver in nodes && receiver.ident.toLowerCase() == "kotlin"
}

fun isSpecialFunction(expr: JsExpression, specialFunction: SpecialFunction): Boolean =
        expr is JsNameRef && expr.qualifier == null && expr.name?.specialFunction == specialFunction

fun Context.isAmdDefine(function: JsExpression): Boolean = isTopLevelFunction(function, "define")

fun Context.isTopLevelFunction(function: JsExpression, name: String): Boolean {
    if (function !is JsNameRef || function.qualifier != null) return false
    return function.ident == name && function.name !in nodes.keys
}

fun JsNode.extractLocation(): JsLocation? {
    return when (this) {
        is SourceInfoAwareJsNode -> source as? JsLocation
        is JsExpressionStatement -> expression.source as? JsLocation
        else -> null
    }
}

fun JsLocation.asString(): String {
    val simpleFileName = file.substring(file.lastIndexOf("/") + 1)
    return "$simpleFileName:${startLine + 1}"
}

fun Set<Node>.extractRoots(): Set<Node> {
    val result = mutableSetOf<Node>()
    val visited = mutableSetOf<Node>()
    forEach { it.original.extractRootsImpl(result, visited) }
    return result
}

private fun Node.extractRootsImpl(target: MutableSet<Node>, visited: MutableSet<Node>) {
    if (!visited.add(original)) return
    val qualifier = original.qualifier
    if (qualifier == null) {
        target += original
    }
    else {
        qualifier.parent.extractRootsImpl(target, visited)
    }
}
