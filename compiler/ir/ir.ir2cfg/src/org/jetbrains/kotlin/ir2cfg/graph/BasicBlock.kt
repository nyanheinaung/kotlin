/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.graph

import org.jetbrains.kotlin.ir.IrStatement

interface BasicBlock : CfgNode {

    val elements: List<IrStatement>

    val incoming: BlockConnector?

    val outgoing: BlockConnector?

    override val predecessors: List<CfgNode>
        get() = listOfNotNull(incoming)

    override val successors: List<CfgNode>
        get() = listOfNotNull(outgoing)
}