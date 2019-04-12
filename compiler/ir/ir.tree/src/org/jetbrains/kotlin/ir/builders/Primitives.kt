/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.builders

import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType

// TODO rewrite using IR Builders

fun primitiveOp1(
    startOffset: Int, endOffset: Int,
    primitiveOpSymbol: IrSimpleFunctionSymbol,
    primitiveOpReturnType: IrType,
    origin: IrStatementOrigin,
    dispatchReceiver: IrExpression
): IrExpression =
    IrCallImpl(startOffset, endOffset, primitiveOpReturnType, primitiveOpSymbol, primitiveOpSymbol.descriptor, origin = origin).also {
        it.dispatchReceiver = dispatchReceiver
    }

fun primitiveOp2(
    startOffset: Int, endOffset: Int,
    primitiveOpSymbol: IrSimpleFunctionSymbol,
    primitiveOpReturnType: IrType,
    origin: IrStatementOrigin,
    argument1: IrExpression, argument2: IrExpression
): IrExpression =
    IrBinaryPrimitiveImpl(startOffset, endOffset, primitiveOpReturnType, origin, primitiveOpSymbol, argument1, argument2)

fun IrGeneratorContext.constNull(startOffset: Int, endOffset: Int): IrExpression =
    IrConstImpl.constNull(startOffset, endOffset, irBuiltIns.nothingNType)

fun IrGeneratorContext.equalsNull(startOffset: Int, endOffset: Int, argument: IrExpression): IrExpression =
    primitiveOp2(
        startOffset, endOffset, irBuiltIns.eqeqSymbol, irBuiltIns.booleanType, IrStatementOrigin.EQEQ,
        argument, constNull(startOffset, endOffset)
    )

fun IrGeneratorContext.eqeqeq(startOffset: Int, endOffset: Int, argument1: IrExpression, argument2: IrExpression): IrExpression =
    primitiveOp2(startOffset, endOffset, irBuiltIns.eqeqeqSymbol, irBuiltIns.booleanType, IrStatementOrigin.EQEQEQ, argument1, argument2)

fun IrGeneratorContext.throwNpe(startOffset: Int, endOffset: Int, origin: IrStatementOrigin): IrExpression =
    IrNullaryPrimitiveImpl(startOffset, endOffset, irBuiltIns.nothingType, origin, irBuiltIns.throwNpeSymbol)

fun IrGeneratorContext.constTrue(startOffset: Int, endOffset: Int) =
    IrConstImpl.constTrue(startOffset, endOffset, irBuiltIns.booleanType)

fun IrGeneratorContext.constFalse(startOffset: Int, endOffset: Int) =
    IrConstImpl.constFalse(startOffset, endOffset, irBuiltIns.booleanType)

fun IrGeneratorContext.elseBranch(elseExpr: IrExpression): IrElseBranch {
    val startOffset = elseExpr.startOffset
    val endOffset = elseExpr.endOffset
    return IrElseBranchImpl(startOffset, endOffset, constTrue(startOffset, endOffset), elseExpr)
}

// a || b == if (a) true else b
fun IrGeneratorContext.oror(
    startOffset: Int,
    endOffset: Int,
    a: IrExpression,
    b: IrExpression,
    origin: IrStatementOrigin = IrStatementOrigin.OROR
): IrWhen =
    IrIfThenElseImpl(startOffset, endOffset, irBuiltIns.booleanType, origin).apply {
        branches.add(IrBranchImpl(a, constTrue(a.startOffset, a.endOffset)))
        branches.add(elseBranch(b))
    }

fun IrGeneratorContext.oror(a: IrExpression, b: IrExpression, origin: IrStatementOrigin = IrStatementOrigin.OROR): IrWhen =
    oror(b.startOffset, b.endOffset, a, b, origin)

fun IrGeneratorContext.whenComma(a: IrExpression, b: IrExpression): IrWhen =
    oror(a, b, IrStatementOrigin.WHEN_COMMA)

// a && b == if (a) b else false
fun IrGeneratorContext.andand(
    startOffset: Int,
    endOffset: Int,
    a: IrExpression,
    b: IrExpression,
    origin: IrStatementOrigin = IrStatementOrigin.ANDAND
): IrWhen =
    IrIfThenElseImpl(startOffset, endOffset, irBuiltIns.booleanType, origin).apply {
        branches.add(IrBranchImpl(a, b))
        branches.add(elseBranch(constFalse(b.startOffset, b.endOffset)))
    }

fun IrGeneratorContext.andand(a: IrExpression, b: IrExpression, origin: IrStatementOrigin = IrStatementOrigin.ANDAND): IrWhen =
    andand(b.startOffset, b.endOffset, a, b, origin)