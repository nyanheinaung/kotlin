/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrReturnableBlock
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrReturnableBlockSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrReturnableBlockSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrBlockImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    origin: IrStatementOrigin? = null
) :
    IrContainerExpressionBase(startOffset, endOffset, type, origin),
    IrBlock {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        origin: IrStatementOrigin?,
        statements: List<IrStatement>
    ) :
            this(startOffset, endOffset, type, origin) {
        this.statements.addAll(statements)
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitBlock(this, data)
}

fun IrBlockImpl.addIfNotNull(statement: IrStatement?) {
    if (statement != null) statements.add(statement)
}

fun IrBlockImpl.inlineStatement(statement: IrStatement) {
    if (statement is IrBlock) {
        statements.addAll(statement.statements)
    } else {
        statements.add(statement)
    }
}

class IrReturnableBlockImpl(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        override val symbol: IrReturnableBlockSymbol,
        origin: IrStatementOrigin? = null,
        override val inlineFunctionSymbol: IrFunctionSymbol? = null
) :
    IrContainerExpressionBase(startOffset, endOffset, type, origin),
    IrReturnableBlock {

    override val descriptor = symbol.descriptor

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrReturnableBlockSymbol,
        origin: IrStatementOrigin?,
        statements: List<IrStatement>,
        inlineFunctionSymbol: IrFunctionSymbol? = null
    ) : this(startOffset, endOffset, type, symbol, origin, inlineFunctionSymbol) {
        this.statements.addAll(statements)
    }

    init {
        symbol.bind(this)
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitBlock(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        statements.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        statements.forEachIndexed { i, irStatement ->
            statements[i] = irStatement.transform(transformer, data)
        }
    }
}
