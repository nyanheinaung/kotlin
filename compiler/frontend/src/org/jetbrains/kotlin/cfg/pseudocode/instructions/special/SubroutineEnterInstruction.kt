/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionWithNext
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl

class SubroutineEnterInstruction(
    val subroutine: KtElement,
    blockScope: BlockScope
) : InstructionWithNext(subroutine, blockScope) {
    override fun accept(visitor: InstructionVisitor) {
        visitor.visitSubroutineEnter(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitSubroutineEnter(this)

    override fun toString(): String = "<START>"

    override fun createCopy(): InstructionImpl =
        SubroutineEnterInstruction(subroutine, blockScope)
}
