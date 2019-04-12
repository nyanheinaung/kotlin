/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions

import org.jetbrains.kotlin.cfg.pseudocode.Pseudocode
import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue

interface Instruction {
    var owner: Pseudocode

    val previousInstructions: Collection<Instruction>
    val nextInstructions: Collection<Instruction>

    val dead: Boolean

    val blockScope: BlockScope

    val inputValues: List<PseudoValue>

    val copies: Collection<Instruction>

    fun accept(visitor: InstructionVisitor)
    fun <R> accept(visitor: InstructionVisitorWithResult<R>): R
}
