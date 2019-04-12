/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode

import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.KtElementInstruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.LocalFunctionDeclarationInstruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.SubroutineEnterInstruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.SubroutineExitInstruction
import org.jetbrains.kotlin.cfg.pseudocode.instructions.special.SubroutineSinkInstruction
import org.jetbrains.kotlin.psi.KtElement

interface Pseudocode {
    val correspondingElement: KtElement

    val parent: Pseudocode?

    val localDeclarations: Set<LocalFunctionDeclarationInstruction>

    val instructions: List<Instruction>

    val reversedInstructions: List<Instruction>

    val instructionsIncludingDeadCode: List<Instruction>

    val exitInstruction: SubroutineExitInstruction

    val errorInstruction: SubroutineExitInstruction

    val sinkInstruction: SubroutineSinkInstruction

    val enterInstruction: SubroutineEnterInstruction

    val isInlined: Boolean
    val containsDoWhile: Boolean
    val rootPseudocode: Pseudocode

    fun getElementValue(element: KtElement?): PseudoValue?

    fun getValueElements(value: PseudoValue?): List<KtElement>

    fun getUsages(value: PseudoValue?): List<Instruction>

    fun isSideEffectFree(instruction: Instruction): Boolean

    fun copy(): Pseudocode

    fun instructionForElement(element: KtElement): KtElementInstruction?
}
