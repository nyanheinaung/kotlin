/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.cfg.pseudocode.instructions.*
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.Label

class UnconditionalJumpInstruction(
    element: KtElement,
    targetLabel: Label,
    blockScope: BlockScope
) : AbstractJumpInstruction(element, targetLabel, blockScope) {
    override fun accept(visitor: InstructionVisitor) {
        visitor.visitUnconditionalJump(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitUnconditionalJump(this)

    override fun toString(): String = "jmp(${targetLabel.name})"

    override fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction =
        UnconditionalJumpInstruction(element, newLabel, blockScope)
}
