/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast

class JsClassModel(val name: JsName, val superName: JsName?) {
    val interfaces: MutableSet<JsName> = mutableSetOf()
    val preDeclarationBlock = JsGlobalBlock()
    val postDeclarationBlock = JsGlobalBlock()
}
