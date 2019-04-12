/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionWithNext
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl
import org.jetbrains.kotlin.psi.*

class VariableDeclarationInstruction(
    element: KtDeclaration,
    blockScope: BlockScope
) : InstructionWithNext(element, blockScope) {
    init {
        assert(element is KtVariableDeclaration || element is KtParameter || element is KtEnumEntry || element is KtObjectDeclaration) {
            "Invalid element: ${render(element)}}"
        }
    }

    val variableDeclarationElement: KtDeclaration
        get() = element as KtDeclaration

    override fun accept(visitor: InstructionVisitor) {
        visitor.visitVariableDeclarationInstruction(this)
    }

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitVariableDeclarationInstruction(this)

    override fun toString(): String = "v(${render(element)})"

    override fun createCopy(): InstructionImpl =
        VariableDeclarationInstruction(variableDeclarationElement, blockScope)
}
