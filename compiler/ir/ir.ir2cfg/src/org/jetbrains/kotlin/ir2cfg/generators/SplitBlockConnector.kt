/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir2cfg.graph.BasicBlock
import org.jetbrains.kotlin.ir2cfg.graph.BlockConnector

class SplitBlockConnector(
        previous: BasicBlock,
        override val element: IrStatement,
        override val nextBlocks: List<BasicBlock>
) : BlockConnector {

    override val previousBlocks = listOf(previous)
}