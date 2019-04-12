/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.pseudocode.Pseudocode
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionWithNext
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl

open class LocalFunctionDeclarationInstruction(
    element: KtElement,
    val body: Pseudocode,
    blockScope: BlockScope
) : InstructionWithNext(element, blockScope) {
    var sink: SubroutineSinkInstruction? = null
        set(value) {
            field = outgoingEdgeTo(value) as SubroutineSinkInstruction?
        }

    override val nextInstructions: Collection<Instruction>
        get() {
            sink?.let {
                val instructions = arrayListOf<Instruction>(it)
                instructions.addAll(super.nextInstructions)
                return instructions
            }
            return super.nextInstructions
        }

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitLocalFunctionDeclarationInstruction(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitLocalFunctionDeclarationInstruction(this)

    override fun toString(): String = "d(${render(element)})"

    override fun createCopy(): InstructionImpl =
        LocalFunctionDeclarationInstruction(element, body.copy(), blockScope)
}
