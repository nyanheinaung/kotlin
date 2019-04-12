/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.psi.KtElement
import java.util.Collections
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult

class SubroutineSinkInstruction(
    val subroutine: KtElement,
    blockScope: BlockScope,
    private val debugLabel: String
) : InstructionImpl(blockScope) {
    override val nextInstructions: Collection<Instruction>
        get() = Collections.emptyList()

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitSubroutineSink(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitSubroutineSink(this)

    override fun toString(): String = debugLabel

    override fun createCopy(): InstructionImpl =
        SubroutineSinkInstruction(subroutine, blockScope, debugLabel)
}
