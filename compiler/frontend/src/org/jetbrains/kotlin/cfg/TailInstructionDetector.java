/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.MagicInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.MergeInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps.AbstractJumpInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps.ThrowExceptionInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.MarkInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.SubroutineExitInstruction;
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.SubroutineSinkInstruction;
import org.jetbrains.kotlin.psi.KtElement;

/**
 * Returns true when visited instruction may lie on a path from a tail-call-like operation to the sink of the subroutine
 */
public class TailInstructionDetector extends InstructionVisitorWithResult<Boolean>  {
    private final KtElement subroutine;

    public TailInstructionDetector(@NotNull KtElement subroutine) {
        this.subroutine = subroutine;
    }

    @Override
    public Boolean visitInstruction(@NotNull Instruction instruction) {
        return false;
    }

    @Override
    public Boolean visitSubroutineExit(@NotNull SubroutineExitInstruction instruction) {
        return !instruction.isError() && instruction.getSubroutine() == subroutine;
    }

    @Override
    public Boolean visitSubroutineSink(@NotNull SubroutineSinkInstruction instruction) {
        return instruction.getSubroutine() == subroutine;
    }

    @Override
    public Boolean visitJump(@NotNull AbstractJumpInstruction instruction) {
        return true;
    }

    @Override
    public Boolean visitThrowExceptionInstruction(@NotNull ThrowExceptionInstruction instruction) {
        return false;
    }

    @Override
    public Boolean visitMarkInstruction(@NotNull MarkInstruction instruction) {
        return true;
    }

    @Override
    public Boolean visitMagic(@NotNull MagicInstruction instruction) {
        return instruction.getSynthetic();
    }

    @Override
    public Boolean visitMerge(@NotNull MergeInstruction instruction) {
        return true;
    }
}
