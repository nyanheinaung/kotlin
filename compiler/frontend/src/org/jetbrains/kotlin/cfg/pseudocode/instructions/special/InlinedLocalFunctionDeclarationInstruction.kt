/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode.instructions.special

import org.jetbrains.kotlin.cfg.pseudocode.Pseudocode
import org.jetbrains.kotlin.cfg.pseudocode.instructions.BlockScope
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionImpl
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitor
import org.jetbrains.kotlin.cfg.pseudocode.instructions.InstructionVisitorWithResult
import org.jetbrains.kotlin.contracts.description.InvocationKind
import org.jetbrains.kotlin.psi.KtElement

class InlinedLocalFunctionDeclarationInstruction(
    element: KtElement,
    body: Pseudocode,
    blockScope: BlockScope,
    val kind: InvocationKind
) : LocalFunctionDeclarationInstruction(element, body, blockScope) {
    override fun createCopy(): InstructionImpl = InlinedLocalFunctionDeclarationInstruction(element, body, blockScope, kind)

    override fun accept(visitor: InstructionVisitor) = visitor.visitInlinedLocalFunctionDeclarationInstruction(this)

    override fun <R> accept(visitor: InstructionVisitorWithResult<R>): R = visitor.visitInlinedFunctionDeclarationInstruction(this)

    override fun toString(): String = "inlined(${render(element)})"
}