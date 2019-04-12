/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionReferenceImpl
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments

class LocalFunctionGenerator(statementGenerator: StatementGenerator) : StatementGeneratorExtension(statementGenerator) {
    fun generateLambda(ktLambda: KtLambdaExpression): IrStatement {
        val ktFun = ktLambda.functionLiteral
        val lambdaExpressionType = getInferredTypeWithImplicitCastsOrFail(ktLambda).toIrType()
        val irLambdaFunction = FunctionGenerator(context).generateLambdaFunctionDeclaration(ktFun)

        val irBlock = IrBlockImpl(ktLambda.startOffset, ktLambda.endOffset, lambdaExpressionType, IrStatementOrigin.LAMBDA)
        irBlock.statements.add(irLambdaFunction)
        irBlock.statements.add(
            IrFunctionReferenceImpl(
                ktLambda.startOffset, ktLambda.endOffset, lambdaExpressionType,
                irLambdaFunction.symbol, irLambdaFunction.symbol.descriptor, 0,
                IrStatementOrigin.LAMBDA
            )
        )
        return irBlock
    }

    fun generateFunction(ktFun: KtNamedFunction): IrStatement =
        if (ktFun.name != null) {
            generateFunctionDeclaration(ktFun)
        } else {
            // anonymous function expression
            val funExpressionType = getInferredTypeWithImplicitCastsOrFail(ktFun).toIrType()
            val irBlock = IrBlockImpl(ktFun.startOffsetSkippingComments, ktFun.endOffset, funExpressionType, IrStatementOrigin.ANONYMOUS_FUNCTION)

            val irFun = generateFunctionDeclaration(ktFun)
            irBlock.statements.add(irFun)

            irBlock.statements.add(
                IrFunctionReferenceImpl(
                    ktFun.startOffsetSkippingComments, ktFun.endOffset, funExpressionType,
                    irFun.symbol, irFun.symbol.descriptor, 0,
                    IrStatementOrigin.ANONYMOUS_FUNCTION
                )
            )

            irBlock
        }

    private fun generateFunctionDeclaration(ktFun: KtNamedFunction): IrFunction =
        FunctionGenerator(context).generateFunctionDeclaration(ktFun)
}