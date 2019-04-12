/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.Label
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.KtElementInstructionImpl
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction

abstract class AbstractJumpInstruction(
    element: KtElement,
    val targetLabel: Label,
    blockScope: BlockScope
) : KtElementInstructionImpl(element, blockScope), JumpInstruction {
    var resolvedTarget: Instruction? = null
        set(value) {
            field = outgoingEdgeTo(value)
        }

    protected abstract fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction

    fun copy(newLabel: Label): Instruction = updateCopyInfo(createCopy(newLabel, blockScope))

    override fun createCopy(): InstructionImpl = createCopy(targetLabel, blockScope)

    override val nextInstructions: Collection<Instruction>
        get() = listOfNotNull(resolvedTarget)
}
