/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.jumps

import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.cfg.Label
import java.util.Collections
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReturnExpression

class ReturnValueInstruction(
    returnExpression: KtExpression,
    blockScope: BlockScope,
    targetLabel: Label,
    val returnedValue: PseudoValue,
    val subroutine: KtElement
) : AbstractJumpInstruction(returnExpression, targetLabel, blockScope) {
    override val inputValues: List<PseudoValue> get() = Collections.singletonList(returnedValue)

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitReturnValue(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitReturnValue(this)

    override fun toString(): String = "ret(*|$returnedValue) $targetLabel"

    override fun createCopy(newLabel: Label, blockScope: BlockScope): AbstractJumpInstruction =
        ReturnValueInstruction((element as KtExpression), blockScope, newLabel, returnedValue, subroutine)

    val returnExpressionIfAny: KtReturnExpression? = element as? KtReturnExpression
}
