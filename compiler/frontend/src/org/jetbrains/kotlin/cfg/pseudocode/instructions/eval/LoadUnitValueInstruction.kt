/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.eval

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionWithNext
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult

class LoadUnitValueInstruction(
    expression: KtExpression,
    blockScope: BlockScope
) : InstructionWithNext(expression, blockScope) {
    override fun accept(visitor: InstructionVisitor) {
        visitor.visitLoadUnitValue(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R {
        return visitor.visitLoadUnitValue(this)
    }

    override fun toString(): String =
        "read (Unit)"

    override fun createCopy(): InstructionImpl =
        LoadUnitValueInstruction(element as KtExpression, blockScope)
}
