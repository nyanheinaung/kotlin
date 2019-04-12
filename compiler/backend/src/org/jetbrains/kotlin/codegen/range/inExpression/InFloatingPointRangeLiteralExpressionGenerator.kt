/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.inExpression

import org.jetbrains.kotlin.codegen.*
import org.jetbrains.kotlin.codegen.range.SimpleBoundedValue
import org.jetbrains.kotlin.codegen.range.comparison.ComparisonGenerator
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class InFloatingPointRangeLiteralExpressionGenerator(
    operatorReference: KtSimpleNameExpression,
    private val rangeLiteral: SimpleBoundedValue,
    private val comparisonGenerator: ComparisonGenerator,
    private val frameMap: FrameMap
) : InExpressionGenerator {
    init {
        assert(rangeLiteral.isLowInclusive && rangeLiteral.isHighInclusive) { "Floating point range literal bounds should be inclusive" }
    }

    private val isNotIn = operatorReference.getReferencedNameElementType() == KtTokens.NOT_IN

    override fun generate(argument: StackValue): BranchedValue =
        gen(argument).let { if (isNotIn) Invert(it) else it }

    private fun gen(argument: StackValue): BranchedValue =
        object : BranchedValue(argument, null, comparisonGenerator.comparedType, Opcodes.IFEQ) {
            override fun condJump(jumpLabel: Label, v: InstructionAdapter, jumpIfFalse: Boolean) {
                if (jumpIfFalse) {
                    genJumpIfFalse(v, jumpLabel)
                } else {
                    genJumpIfTrue(v, jumpLabel)
                }
            }

            private fun genJumpIfTrue(v: InstructionAdapter, jumpLabel: Label) {
                //  if (arg is in range) goto jumpLabel
                // =>
                //      if (arg is NOT in range) goto exitLabel
                //      goto jumpLabel
                //  exitLabel:

                frameMap.useTmpVar(operandType) { argVar ->
                    val exitLabel = Label()
                    genJumpIfFalse(v, exitLabel)
                    v.goTo(jumpLabel)
                    v.mark(exitLabel)
                }
            }

            private fun genJumpIfFalse(v: InstructionAdapter, jumpLabel: Label) {
                // if (arg is NOT in range) goto jumpLabel

                frameMap.useTmpVar(operandType) { argVar ->
                    // Evaluate low and high bounds once (unless they have no side effects)
                    val (lowValue, lowTmpType) = introduceTemporaryIfRequired(v, rangeLiteral.lowBound, operandType)
                    val (highValue, highTmpType) = introduceTemporaryIfRequired(v, rangeLiteral.highBound, operandType)

                    val argValue = StackValue.local(argVar, operandType)
                    argValue.store(arg1, v)

                    // if (low bound is NOT satisfied) goto jumpLabel
                    // arg < low
                    argValue.put(operandType, v)
                    lowValue.put(operandType, v)
                    comparisonGenerator.jumpIfLess(v, jumpLabel)

                    // if (high bound is NOT satisfied) goto jumpLabel
                    // arg > high
                    argValue.put(operandType, v)
                    highValue.put(operandType, v)
                    comparisonGenerator.jumpIfGreater(v, jumpLabel)

                    highTmpType?.let { frameMap.leaveTemp(it) }
                    lowTmpType?.let { frameMap.leaveTemp(it) }
                }

            }

            // TODO evaluateOnce
            private fun introduceTemporaryIfRequired(v: InstructionAdapter, value: StackValue, type: Type): Pair<StackValue, Type?> {
                val resultValue: StackValue
                val resultType: Type?

                if (value.canHaveSideEffects()) {
                    val index = frameMap.enterTemp(type)
                    resultValue = StackValue.local(index, type)
                    resultType = type
                    resultValue.store(value, v)
                } else {
                    resultValue = value
                    resultType = null
                }

                return Pair(resultValue, resultType)
            }
        }
}