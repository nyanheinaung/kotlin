/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.cfg.Label
import java.util.Collections
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult

class ThrowExceptionInstruction(
    expression: KtThrowExpression,
    blockScope: BlockScope,
    errorLabel: Label,
    private val thrownValue: PseudoValue
) : AbstractJumpInstruction(expression, errorLabel, blockScope) {
    override val inputValues: List<PseudoValue> get() = Collections.singletonList(thrownValue)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitThrowExceptionInstruction(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitThrowExceptionInstruction(this)

    override fun toString(): String = "throw (${element.text}|$thrownValue)"

    override fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction =
        ThrowExceptionInstruction((element as KtThrowExpression), blockScope, newLabel, thrownValue)
}
