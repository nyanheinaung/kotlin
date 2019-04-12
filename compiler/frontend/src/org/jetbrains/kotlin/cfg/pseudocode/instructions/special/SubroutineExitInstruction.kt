/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.cfg.pseudocode.instructions.*
import org.jetbrains.kotlin.psi.KtElement
import java.util.*

class SubroutineExitInstruction(
    val subroutine: KtElement,
    blockScope: BlockScope,
    val isError: Boolean
) : InstructionImpl(blockScope) {
    private var _sink: SubroutineSinkInstruction? = null

    var sink: SubroutineSinkInstruction
        get() = _sink!!
        set(value: SubroutineSinkInstruction) {
            _sink = outgoingEdgeTo(value) as SubroutineSinkInstruction
        }

    override val nextInstructions: Collection<Instruction>
        get() = Collections.singleton(sink)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitSubroutineExit(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitSubroutineExit(this)

    override fun toString(): String = if (isError) "<ERROR>" else "<END>"

    override fun createCopy(): InstructionImpl =
        SubroutineExitInstruction(subroutine, blockScope, isError)
}
