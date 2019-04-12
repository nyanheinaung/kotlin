/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.dce

import org.jetbrains.kotlin.js.backend.ast.JsFunction
import org.jetbrains.kotlin.js.backend.ast.JsInvocation
import org.jetbrains.kotlin.js.backend.ast.JsNode
import org.jetbrains.kotlin.js.dce.Context.Node

interface AnalysisResult {
    val nodeMap: Map<JsNode, Node>

    val astNodesToEliminate: Set<JsNode>

    val astNodesToSkip: Set<JsNode>

    val functionsToEnter: Set<JsFunction>

    val invocationsToSkip: Set<JsInvocation>

    val functionsToSkip: Set<Context.Node>
}
