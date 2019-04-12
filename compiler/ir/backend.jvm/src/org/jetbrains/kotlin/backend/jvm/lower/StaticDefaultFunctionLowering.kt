/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.lower

import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.ClassLoweringPass
import org.jetbrains.kotlin.backend.common.phaser.makeIrFilePhase
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

internal val staticDefaultFunctionPhase = makeIrFilePhase(
    ::StaticDefaultFunctionLowering,
    name = "StaticDefaultFunction",
    description = "Generate static functions for default parameters"
)

private class StaticDefaultFunctionLowering() : IrElementTransformerVoid(), ClassLoweringPass {
    constructor(@Suppress("UNUSED_PARAMETER") context: BackendContext) : this()

    val updatedFunctions = hashMapOf<IrFunctionSymbol, IrFunction>()

    override fun lower(irClass: IrClass) {
        irClass.accept(this, null)
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        return if (declaration.origin == IrDeclarationOrigin.FUNCTION_FOR_DEFAULT_PARAMETER && declaration.dispatchReceiverParameter != null) {
            return createStaticFunctionWithReceivers(declaration.parent, declaration.name, declaration).also {
                updatedFunctions[declaration.symbol] = it
                super.visitFunction(declaration)
            }
        } else {
            super.visitFunction(declaration)
        }
    }

    override fun visitReturn(expression: IrReturn): IrExpression {
        return super.visitReturn(
            if (updatedFunctions.containsKey(expression.returnTargetSymbol)) {
                with(expression) {
                    val irFunction = updatedFunctions[expression.returnTargetSymbol]!!
                    IrReturnImpl(startOffset, endOffset, expression.type, irFunction.symbol, expression.value)
                }
            } else {
                expression
            }
        )
    }
}