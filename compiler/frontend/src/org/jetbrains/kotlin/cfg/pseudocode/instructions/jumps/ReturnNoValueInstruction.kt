/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.Label
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult

class ReturnNoValueInstruction(
    element: KtElement,
    blockScope: BlockScope,
    targetLabel: Label,
    val subroutine: KtElement
) : AbstractJumpInstruction(element, targetLabel, blockScope) {
    override fun accept(visitor: InstructionVisitor) {
        visitor.visitReturnNoValue(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitReturnNoValue(this)

    override fun toString(): String = "ret $targetLabel"

    override fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction =
        ReturnNoValueInstruction(element, blockScope, newLabel, subroutine)
}
