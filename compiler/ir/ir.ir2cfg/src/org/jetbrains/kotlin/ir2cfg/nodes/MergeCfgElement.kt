/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir2cfg.nodes

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class MergeCfgElement(val from: IrElement, val name: String) : CfgIrElement {
    override val startOffset = from.startOffset
    override val endOffset = from.endOffset

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D) = visitor.visitElement(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) = Unit

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) = Unit

    override fun toString() = "$name: ${from.dump()}"
}