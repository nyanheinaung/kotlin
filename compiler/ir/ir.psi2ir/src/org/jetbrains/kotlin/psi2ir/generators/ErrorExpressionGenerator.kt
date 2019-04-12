/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrErrorCallExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrErrorExpressionImpl
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffsetSkippingComments
import org.jetbrains.kotlin.types.ErrorUtils

class ErrorExpressionGenerator(statementGenerator: StatementGenerator) : StatementGeneratorExtension(statementGenerator) {
    private val ignoreErrors: Boolean get() = context.configuration.ignoreErrors

    private inline fun generateErrorExpression(ktElement: KtElement, e: Exception? = null, body: () -> IrExpression) =
        if (ignoreErrors)
            body()
        else
            throw RuntimeException("${e?.message}: ${ktElement::class.java.simpleName}:\n${ktElement.text}", e)

    fun generateErrorExpression(ktElement: KtElement, e: Exception): IrExpression =
        generateErrorExpression(ktElement, e) {
            val errorExpressionType =
                if (ktElement is KtExpression)
                    getErrorExpressionType(ktElement)
                else
                    ErrorUtils.createErrorType("")
            IrErrorExpressionImpl(
                ktElement.startOffsetSkippingComments, ktElement.endOffset,
                errorExpressionType.toIrType(),
                e.message ?: ""
            )
        }

    fun generateErrorCall(ktCall: KtCallExpression): IrExpression = generateErrorExpression(ktCall) {
        val type = getErrorExpressionType(ktCall).toIrType()

        val irErrorCall = IrErrorCallExpressionImpl(ktCall.startOffsetSkippingComments, ktCall.endOffset, type, "") // TODO problem description?
        irErrorCall.explicitReceiver = (ktCall.parent as? KtDotQualifiedExpression)?.run {
            receiverExpression.genExpr()
        }

        (ktCall.valueArguments + ktCall.lambdaArguments).forEach {
            val ktArgument = it.getArgumentExpression()
            if (ktArgument != null) {
                irErrorCall.addArgument(ktArgument.genExpr())
            }
        }

        irErrorCall
    }

    private fun getErrorExpressionType(ktExpression: KtExpression) =
        getInferredTypeWithImplicitCasts(ktExpression) ?: ErrorUtils.createErrorType("")

    fun generateErrorSimpleName(ktName: KtSimpleNameExpression): IrExpression = generateErrorExpression(ktName) {
        val type = getErrorExpressionType(ktName).toIrType()

        val irErrorCall = IrErrorCallExpressionImpl(ktName.startOffsetSkippingComments, ktName.endOffset, type, "") // TODO problem description?
        irErrorCall.explicitReceiver = (ktName.parent as? KtDotQualifiedExpression)?.let { ktParent ->
            if (ktParent.receiverExpression == ktName) null
            else ktParent.receiverExpression.genExpr()
        }

        irErrorCall
    }

}