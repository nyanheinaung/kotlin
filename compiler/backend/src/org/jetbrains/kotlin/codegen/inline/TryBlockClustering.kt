/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.optimization.common.InsnSequence
import org.jetbrains.org.objectweb.asm.tree.LabelNode
import org.jetbrains.org.objectweb.asm.tree.TryCatchBlockNode

enum class TryCatchPosition {
    START,
    END,
    INNER
}

class SplitPair<out T : Interval>(val patchedPart: T, val newPart: T)

class SimpleInterval(override val startLabel: LabelNode, override val endLabel: LabelNode) : Interval

interface Interval {
    val startLabel: LabelNode
    val endLabel: LabelNode

    /*note that some intervals are mutable */
    fun isEmpty(): Boolean = startLabel == endLabel

    fun verify(processor: CoveringTryCatchNodeProcessor) {
        assert(processor.instructionIndex(startLabel) <= processor.instructionIndex(endLabel)) {
            "Try block body starts after body end: ${processor.instructionIndex(startLabel)} > ${processor.instructionIndex(endLabel)}"
        }
    }
}

interface SplittableInterval<out T : Interval> : Interval {
    fun split(splitBy: Interval, keepStart: Boolean): SplitPair<T>
}

interface IntervalWithHandler : Interval {
    val handler: LabelNode
    val type: String?
}

class TryCatchBlockNodeInfo(
    val node: TryCatchBlockNode,
    val onlyCopyNotProcess: Boolean
) : IntervalWithHandler, SplittableInterval<TryCatchBlockNodeInfo> {
    override val startLabel: LabelNode
        get() = node.start
    override val endLabel: LabelNode
        get() = node.end
    override val handler: LabelNode
        get() = node.handler
    override val type: String?
        get() = node.type

    override fun split(splitBy: Interval, keepStart: Boolean): SplitPair<TryCatchBlockNodeInfo> {
        val newPartInterval = if (keepStart) {
            val oldEnd = endLabel
            node.end = splitBy.startLabel
            Pair(splitBy.endLabel, oldEnd)
        } else {
            val oldStart = startLabel
            node.start = splitBy.endLabel
            Pair(oldStart, splitBy.startLabel)
        }
        return SplitPair(
            this,
            TryCatchBlockNodeInfo(TryCatchBlockNode(newPartInterval.first, newPartInterval.second, handler, type), onlyCopyNotProcess)
        )
    }
}

val TryCatchBlockNodeInfo.bodyInstuctions
    get() = InsnSequence(startLabel, endLabel)

class TryCatchBlockNodePosition(
    val nodeInfo: TryCatchBlockNodeInfo,
    var position: TryCatchPosition
) : IntervalWithHandler by nodeInfo

class TryBlockCluster<T : IntervalWithHandler>(val blocks: MutableList<T>) {
    val defaultHandler: T?
        get() = blocks.firstOrNull() { it.type == null }
}

fun <T : IntervalWithHandler> doClustering(blocks: List<T>): List<TryBlockCluster<T>> {
    data class TryBlockInterval(val startLabel: LabelNode, val endLabel: LabelNode)

    val clusters = linkedMapOf<TryBlockInterval, TryBlockCluster<T>>()
    blocks.forEach { block ->
        val interval = TryBlockInterval(firstLabelInChain(block.startLabel), firstLabelInChain(block.endLabel))
        val cluster = clusters.getOrPut(interval, { TryBlockCluster(arrayListOf()) })
        cluster.blocks.add(block)
    }

    return clusters.values.toList()
}
