/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.graph

interface CfgNode {

    val predecessors: List<CfgNode>

    val successors: List<CfgNode>
}