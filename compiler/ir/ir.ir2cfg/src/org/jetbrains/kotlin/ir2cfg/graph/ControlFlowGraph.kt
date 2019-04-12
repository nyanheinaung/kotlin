/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.graph

import org.jetbrains.kotlin.ir.declarations.IrFunction

interface ControlFlowGraph {

    val function: IrFunction

    // First block is the entry point
    val blocks: List<BasicBlock>

    val connectors: List<BlockConnector>

    val nodes: List<CfgNode>
        get() = blocks + connectors
}