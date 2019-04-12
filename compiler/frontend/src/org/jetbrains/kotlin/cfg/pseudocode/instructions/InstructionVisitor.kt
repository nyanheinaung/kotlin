/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions

import org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps.*
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.*
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.*

open class InstructionVisitor {
    open fun visitAccessInstruction(instruction: AccessValueInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitReadValue(instruction: ReadValueInstruction) {
        visitAccessInstruction(instruction)
    }

    open fun visitLocalFunctionDeclarationInstruction(instruction: LocalFunctionDeclarationInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitInlinedLocalFunctionDeclarationInstruction(instruction: InlinedLocalFunctionDeclarationInstruction) {
        visitLocalFunctionDeclarationInstruction(instruction)
    }

    open fun visitVariableDeclarationInstruction(instruction: VariableDeclarationInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitUnconditionalJump(instruction: UnconditionalJumpInstruction) {
        visitJump(instruction)
    }

    open fun visitConditionalJump(instruction: ConditionalJumpInstruction) {
        visitJump(instruction)
    }

    open fun visitReturnValue(instruction: ReturnValueInstruction) {
        visitJump(instruction)
    }

    open fun visitReturnNoValue(instruction: ReturnNoValueInstruction) {
        visitJump(instruction)
    }

    open fun visitThrowExceptionInstruction(instruction: ThrowExceptionInstruction) {
        visitJump(instruction)
    }

    open fun visitNondeterministicJump(instruction: NondeterministicJumpInstruction) {
        visitInstruction(instruction)
    }

    open fun visitSubroutineExit(instruction: SubroutineExitInstruction) {
        visitInstruction(instruction)
    }

    open fun visitSubroutineSink(instruction: SubroutineSinkInstruction) {
        visitInstruction(instruction)
    }

    open fun visitJump(instruction: AbstractJumpInstruction) {
        visitInstruction(instruction)
    }

    open fun visitInstructionWithNext(instruction: InstructionWithNext) {
        visitInstruction(instruction)
    }

    open fun visitInstruction(instruction: Instruction) {
    }

    open fun visitSubroutineEnter(instruction: SubroutineEnterInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitWriteValue(instruction: WriteValueInstruction) {
        visitAccessInstruction(instruction)
    }

    open fun visitLoadUnitValue(instruction: LoadUnitValueInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitOperation(instruction: OperationInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitCallInstruction(instruction: CallInstruction) {
        visitOperation(instruction)
    }

    open fun visitMerge(instruction: MergeInstruction) {
        visitOperation(instruction)
    }

    open fun visitMarkInstruction(instruction: MarkInstruction) {
        visitInstructionWithNext(instruction)
    }

    open fun visitMagic(instruction: MagicInstruction) {
        visitOperation(instruction)
    }
}
