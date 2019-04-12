/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.common

import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

interface VarFrame<F : VarFrame<F>> {
    fun mergeFrom(other: F)
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

interface BackwardAnalysisInterpreter<F : VarFrame<F>> {
    fun newFrame(maxLocals: Int): F
    fun def(frame: F, insn: AbstractInsnNode)
    fun use(frame: F, insn: AbstractInsnNode)
}

fun <F : VarFrame<F>> analyze(node: MethodNode, interpreter: BackwardAnalysisInterpreter<F>): List<F> {
    val graph = ControlFlowGraph.build(node)
    val insnList = node.instructions

    val frames = (1..insnList.size()).map { interpreter.newFrame(node.maxLocals) }.toMutableList()
    val insnArray = insnList.toArray()

    // see Figure 9.16 from Dragon book
    var wereChanges: Boolean

    do {
        wereChanges = false
        for (insn in insnArray) {
            val index = insnList.indexOf(insn)
            val newFrame = interpreter.newFrame(node.maxLocals)
            for (successorIndex in graph.getSuccessorsIndices(insn)) {
                newFrame.mergeFrom(frames[successorIndex])
            }

            interpreter.def(newFrame, insn)
            interpreter.use(newFrame, insn)

            if (frames[index] != newFrame) {
                frames[index] = newFrame
                wereChanges = true
            }
        }

    } while (wereChanges)

    return frames
}
