/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.generators

import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.lexer.KtTokens


fun getInfixOperator(ktOperator: IElementType): IrStatementOrigin? =
    when (ktOperator) {
        KtTokens.EQ -> IrStatementOrigin.EQ
        KtTokens.PLUSEQ -> IrStatementOrigin.PLUSEQ
        KtTokens.MINUSEQ -> IrStatementOrigin.MINUSEQ
        KtTokens.MULTEQ -> IrStatementOrigin.MULTEQ
        KtTokens.DIVEQ -> IrStatementOrigin.DIVEQ
        KtTokens.PERCEQ -> IrStatementOrigin.PERCEQ
        KtTokens.PLUS -> IrStatementOrigin.PLUS
        KtTokens.MINUS -> IrStatementOrigin.MINUS
        KtTokens.MUL -> IrStatementOrigin.MUL
        KtTokens.DIV -> IrStatementOrigin.DIV
        KtTokens.PERC -> IrStatementOrigin.PERC
        KtTokens.RANGE -> IrStatementOrigin.RANGE
        KtTokens.LT -> IrStatementOrigin.LT
        KtTokens.LTEQ -> IrStatementOrigin.LTEQ
        KtTokens.GT -> IrStatementOrigin.GT
        KtTokens.GTEQ -> IrStatementOrigin.GTEQ
        KtTokens.EQEQ -> IrStatementOrigin.EQEQ
        KtTokens.EXCLEQ -> IrStatementOrigin.EXCLEQ
        KtTokens.EQEQEQ -> IrStatementOrigin.EQEQEQ
        KtTokens.EXCLEQEQEQ -> IrStatementOrigin.EXCLEQEQ
        KtTokens.IN_KEYWORD -> IrStatementOrigin.IN
        KtTokens.NOT_IN -> IrStatementOrigin.NOT_IN
        KtTokens.ANDAND -> IrStatementOrigin.ANDAND
        KtTokens.OROR -> IrStatementOrigin.OROR
        KtTokens.ELVIS -> IrStatementOrigin.ELVIS
        else -> null
    }

fun getPrefixOperator(ktOperator: IElementType): IrStatementOrigin? =
    when (ktOperator) {
        KtTokens.PLUSPLUS -> IrStatementOrigin.PREFIX_INCR
        KtTokens.MINUSMINUS -> IrStatementOrigin.PREFIX_DECR
        KtTokens.EXCL -> IrStatementOrigin.EXCL
        KtTokens.MINUS -> IrStatementOrigin.UMINUS
        KtTokens.PLUS -> IrStatementOrigin.UPLUS
        else -> null
    }

fun getPostfixOperator(ktOperator: IElementType): IrStatementOrigin? =
    when (ktOperator) {
        KtTokens.PLUSPLUS -> IrStatementOrigin.POSTFIX_INCR
        KtTokens.MINUSMINUS -> IrStatementOrigin.POSTFIX_DECR
        KtTokens.EXCLEXCL -> IrStatementOrigin.EXCLEXCL
        else -> null
    }

fun getIrTypeOperator(ktOperator: IElementType): IrTypeOperator? =
    when (ktOperator) {
        KtTokens.IS_KEYWORD -> IrTypeOperator.INSTANCEOF
        KtTokens.NOT_IS -> IrTypeOperator.NOT_INSTANCEOF
        KtTokens.AS_KEYWORD -> IrTypeOperator.CAST
        KtTokens.AS_SAFE -> IrTypeOperator.SAFE_CAST
        else -> null
    }

val AUGMENTED_ASSIGNMENTS =
    setOf(IrStatementOrigin.PLUSEQ, IrStatementOrigin.MINUSEQ, IrStatementOrigin.MULTEQ, IrStatementOrigin.DIVEQ, IrStatementOrigin.PERCEQ)

val OPERATORS_DESUGARED_TO_CALLS =
    setOf(
        IrStatementOrigin.PLUS,
        IrStatementOrigin.MINUS,
        IrStatementOrigin.MUL,
        IrStatementOrigin.DIV,
        IrStatementOrigin.PERC,
        IrStatementOrigin.RANGE,
        IrStatementOrigin.EXCL,
        IrStatementOrigin.UMINUS,
        IrStatementOrigin.UPLUS
    )

val COMPARISON_OPERATORS =
    setOf(IrStatementOrigin.LT, IrStatementOrigin.LTEQ, IrStatementOrigin.GT, IrStatementOrigin.GTEQ)

val EQUALITY_OPERATORS =
    setOf(IrStatementOrigin.EQEQ, IrStatementOrigin.EXCLEQ)

val IDENTITY_OPERATORS =
    setOf(IrStatementOrigin.EQEQEQ, IrStatementOrigin.EXCLEQEQ)

val IN_OPERATORS =
    setOf(IrStatementOrigin.IN, IrStatementOrigin.NOT_IN)

val BINARY_BOOLEAN_OPERATORS =
    setOf(IrStatementOrigin.ANDAND, IrStatementOrigin.OROR)

val INCREMENT_DECREMENT_OPERATORS =
    setOf(IrStatementOrigin.PREFIX_INCR, IrStatementOrigin.PREFIX_DECR, IrStatementOrigin.POSTFIX_INCR, IrStatementOrigin.POSTFIX_DECR)

val POSTFIX_INCREMENT_DECREMENT_OPERATORS =
    setOf(IrStatementOrigin.POSTFIX_INCR, IrStatementOrigin.POSTFIX_DECR)