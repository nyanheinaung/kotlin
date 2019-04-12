/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.Label
import java.util.Arrays
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor

class ConditionalJumpInstruction(
    element: KtElement,
    val onTrue: Boolean,
    blockScope: BlockScope,
    targetLabel: Label,
    private val conditionValue: PseudoValue?
) : AbstractJumpInstruction(element, targetLabel, blockScope) {
    private var _nextOnTrue: Instruction? = null
    private var _nextOnFalse: Instruction? = null

    var nextOnTrue: Instruction
        get() = _nextOnTrue!!
        set(value) {
            _nextOnTrue = outgoingEdgeTo(value)
        }

    var nextOnFalse: Instruction
        get() = _nextOnFalse!!
        set(value) {
            _nextOnFalse = outgoingEdgeTo(value)
        }

    override val nextInstructions: Collection<Instruction>
        get() = Arrays.asList(nextOnFalse, nextOnTrue)

    override val inputValues: List<PseudoValue>
        get() = listOfNotNull(conditionValue)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitConditionalJump(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R {
        return visitor.visitConditionalJump(this)
    }

    override fun toString(): String {
        val instr = if (onTrue) "jt" else "jf"
        val inValue = conditionValue?.let { "|" + it } ?: ""
        return "$instr(${targetLabel.name}$inValue)"
    }

    override fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction =
        ConditionalJumpInstruction(element, onTrue, blockScope, newLabel, conditionValue)
}
