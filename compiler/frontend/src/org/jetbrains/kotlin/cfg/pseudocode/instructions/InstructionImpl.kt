/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions

import java.util.Collections
import java.util.LinkedHashSet
import org.jetbrains.kotlin.cfg.pseudocode.Pseudocode
import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue

abstract class InstructionImpl(override val blockScope: BlockScope) : Instruction {
    private var _owner: Pseudocode? = null

    override var owner: Pseudocode
        get() = _owner!!
        set(value) {
            assert(_owner == null || _owner == value)
            _owner = value
        }

    private var allCopies: MutableSet<InstructionImpl>? = null

    override val copies: Collection<Instruction>
        get() = allCopies?.filter { it != this } ?: Collections.emptyList()

    fun copy(): Instruction = updateCopyInfo(createCopy())

    protected abstract fun createCopy(): InstructionImpl

    protected fun updateCopyInfo(instruction: InstructionImpl): Instruction {
        if (allCopies == null) {
            allCopies = hashSetOf(this)
        }
        instruction.allCopies = allCopies
        allCopies!!.add(instruction)
        return instruction
    }

    var markedAsDead: Boolean = false

    override val dead: Boolean get() = allCopies?.all { it.markedAsDead } ?: markedAsDead

    override val previousInstructions: MutableCollection<Instruction> = LinkedHashSet()

    protected fun outgoingEdgeTo(target: Instruction?): Instruction? {
        (target as InstructionImpl?)?.previousInstructions?.add(this)
        return target
    }

    override val inputValues: List<PseudoValue> = Collections.emptyList()
}
