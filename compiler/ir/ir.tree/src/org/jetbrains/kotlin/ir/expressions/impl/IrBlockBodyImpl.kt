/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.IrElementBase
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import java.util.*

class IrBlockBodyImpl(
    startOffset: Int,
    endOffset: Int
) :
    IrElementBase(startOffset, endOffset),
    IrBlockBody {

    constructor(startOffset: Int, endOffset: Int, statements: List<IrStatement>) : this(startOffset, endOffset) {
        this.statements.addAll(statements)
    }

    override val statements: MutableList<IrStatement> = ArrayList()

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitBlockBody(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        statements.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        statements.forEachIndexed { i, irStatement ->
            statements[i] = irStatement.transform(transformer, data)
        }
    }
}