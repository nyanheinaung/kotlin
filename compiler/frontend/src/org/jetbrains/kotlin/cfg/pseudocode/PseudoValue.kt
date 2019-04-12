/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.InstructionWithValue

interface PseudoValue {
    val debugName: String
    val element: KtElement?
    val createdAt: InstructionWithValue?
}

interface PseudoValueFactory {
    fun newValue(element: KtElement?, instruction: InstructionWithValue?): PseudoValue
}
