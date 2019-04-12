/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.optimization.common.InsnSequence
import org.jetbrains.org.objectweb.asm.tree.LineNumberNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode
import java.util.*

//TODO comment
class SMAPAndMethodNode(val node: MethodNode, val classSMAP: SMAP) {
    val sortedRanges = createLineNumberSequence(node, classSMAP).distinct().toList().sortedWith(RangeMapping.Comparator)

    fun copyWithNewNode(newMethodNode: MethodNode) = SMAPAndMethodNode(newMethodNode, classSMAP)
}

private fun createLineNumberSequence(node: MethodNode, classSMAP: SMAP): Sequence<RangeMapping> {
    return InsnSequence(node.instructions.first, null).filterIsInstance<LineNumberNode>().map { lineNumber ->
        val index = classSMAP.intervals.binarySearch(RangeMapping(lineNumber.line, lineNumber.line, 1), Comparator { value, key ->
            if (key.dest in value) 0 else RangeMapping.Comparator.compare(value, key)
        })
        if (index < 0) {
            error("Unmapped line number in inlined function ${node.name}:${lineNumber.line}")
        }
        classSMAP.intervals[index]
    }
}
