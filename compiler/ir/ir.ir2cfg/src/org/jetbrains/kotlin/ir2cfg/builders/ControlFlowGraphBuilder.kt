/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.builders

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir2cfg.graph.ControlFlowGraph

interface ControlFlowGraphBuilder {
    // Add element to the builder pointer
    fun add(element: IrStatement)

    // Move builder pointer to element without changing graph
    fun move(to: IrStatement)

    // Connect builder pointer with the given element and move pointer to this element
    fun jump(to: IrStatement)

    // Connect from with to and move pointer to the destination
    fun jump(to: IrStatement, from: IrStatement)

    // Build CFG from builder
    fun build(): ControlFlowGraph
}