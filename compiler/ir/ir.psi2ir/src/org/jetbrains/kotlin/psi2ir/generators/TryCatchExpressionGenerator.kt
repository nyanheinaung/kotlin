/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCatchImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments
import org.jetbrains.kotlin.resolve.BindingContext

class TryCatchExpressionGenerator(statementGenerator: StatementGenerator) : StatementGeneratorExtension(statementGenerator) {
    fun generateTryCatch(ktTry: KtTryExpression): IrExpression {
        val resultType = getInferredTypeWithImplicitCastsOrFail(ktTry).toIrType()
        val irTryCatch = IrTryImpl(ktTry.startOffsetSkippingComments, ktTry.endOffset, resultType)

        irTryCatch.tryResult = ktTry.tryBlock.genExpr()

        for (ktCatchClause in ktTry.catchClauses) {
            val ktCatchParameter = ktCatchClause.catchParameter!!
            val ktCatchBody = ktCatchClause.catchBody!!
            val catchParameterDescriptor = getOrFail(BindingContext.VALUE_PARAMETER, ktCatchParameter)

            val irCatch = IrCatchImpl(
                ktCatchClause.startOffsetSkippingComments, ktCatchClause.endOffset,
                context.symbolTable.declareVariable(
                    ktCatchParameter.startOffsetSkippingComments, ktCatchParameter.endOffset,
                    IrDeclarationOrigin.CATCH_PARAMETER,
                    catchParameterDescriptor, catchParameterDescriptor.type.toIrType()
                )
            ).apply {
                result = ktCatchBody.genExpr()
            }

            irTryCatch.catches.add(irCatch)
        }

        irTryCatch.finallyExpression = ktTry.finallyBlock?.run { finalExpression.genExpr() }

        return irTryCatch
    }
}