/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir2cfg.builders.BlockConnectorBuilder
import org.jetbrains.kotlin.ir2cfg.graph.BasicBlock

class GeneralConnectorBuilder(private val element: IrStatement) : BlockConnectorBuilder {

    private val next = linkedSetOf<BasicBlock>()

    private val previous = linkedSetOf<BasicBlock>()

    override fun addNext(basicBlock: BasicBlock) {
        next.add(basicBlock)
    }

    override fun addPrevious(basicBlock: BasicBlock) {
        previous.add(basicBlock)
    }

    override fun build() = when {
        next.size <= 1 -> JoinBlockConnector(previous.toList(), element, next.firstOrNull())
        previous.size == 1 -> SplitBlockConnector(previous.single(), element, next.toList())
        else -> throw AssertionError("Connector should have either exactly one previous block or no more than one next block, " +
                                     "actual previous = ${previous.size}, next = ${next.size}")
    }
}