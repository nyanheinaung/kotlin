/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir2cfg.graph.BasicBlock
import org.jetbrains.kotlin.ir2cfg.graph.BlockConnector
import org.jetbrains.kotlin.ir2cfg.graph.ControlFlowGraph
import org.jetbrains.kotlin.ir2cfg.nodes.MergeCfgElement

private fun IrElement.cfgDump() = when (this) {
    is MergeCfgElement -> "$this"
    else -> dump()
}

fun BasicBlock.dump(builder: StringBuilder = StringBuilder(), indent: String = ""): String {
    for ((index, element) in elements.withIndex()) {
        builder.append(indent)
        builder.append(String.format("%3d ", index + 1))
        val dump = element.cfgDump()
        builder.appendln(dump.lines().first())
    }
    return builder.toString()
}

fun BlockConnector.dump(builder: StringBuilder = StringBuilder(), indent: String = ""): String {
    builder.append(indent)
    val dump = element.cfgDump()
    builder.appendln(dump.lines().first())
    return builder.toString()
}

fun ControlFlowGraph.dump(): String {
    val connectorIndex = hashMapOf<BlockConnector, Int>()
    for ((index, connector) in connectors.withIndex()) {
        connectorIndex[connector] = index
    }
    val blockIndex = hashMapOf<BasicBlock, Int>()
    for ((index, block) in blocks.withIndex()) {
        blockIndex[block] = index
    }
    val builder = StringBuilder()
    for ((index, block) in blocks.withIndex()) {
        builder.appendln("BB $index")
        val incoming = block.incoming
        if (incoming != null) {
            builder.appendln(incoming.previousBlocks.joinToString(prefix = "INCOMING <- BB ") { blockIndex[it].toString() })
            incoming.dump(builder, "    ")
        }
        builder.appendln("CONTENT")
        block.dump(builder, "    ")
        val outgoing = block.outgoing
        if (outgoing != null) {
            if (outgoing.nextBlocks.isEmpty()) {
                builder.appendln("OUTGOING -> NONE")
            }
            else {
                builder.appendln(outgoing.nextBlocks.joinToString(prefix = "OUTGOING -> BB ") { blockIndex[it].toString() })
            }
            outgoing.dump(builder, "    ")
        }
    }
    return builder.toString()
}