/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode

import org.jetbrains.kotlin.cfg.Label
import org.jetbrains.kotlin.cfg.pseudocode.instructions.Instruction
import org.jetbrains.kotlin.psi.KtElement

class PseudocodeLabel internal constructor(
    override val pseudocode: PseudocodeImpl, override val name: String, private val comment: String?
) : Label {

    private val instructionList: List<Instruction> get() = pseudocode.mutableInstructionList

    private val correspondingElement: KtElement get() = pseudocode.correspondingElement

    override var targetInstructionIndex = -1

    override fun toString(): String = if (comment == null) name else "$name [$comment]"

    override fun resolveToInstruction(): Instruction {
        val index = targetInstructionIndex
        when {
            index < 0 ->
                error(
                    "resolveToInstruction: unbound label $name " +
                            "in subroutine ${correspondingElement.text} with instructions $instructionList"
                )
            index >= instructionList.size ->
                error(
                    "resolveToInstruction: incorrect index $index for label $name " +
                            "in subroutine ${correspondingElement.text} with instructions $instructionList"
                )
            else ->
                return instructionList[index]
        }
    }

    fun copy(newPseudocode: PseudocodeImpl, newLabelIndex: Int): PseudocodeLabel =
        PseudocodeLabel(newPseudocode, "L" + newLabelIndex, "copy of $name, $comment")
}
