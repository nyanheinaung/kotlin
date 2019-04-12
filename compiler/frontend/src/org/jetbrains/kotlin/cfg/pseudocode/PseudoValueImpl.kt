/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg.pseudocode

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.InstructionWithValue

class PseudoValueImpl(
    override val debugName: String,
    override val element: KtElement?,
    override val createdAt: InstructionWithValue?
) : PseudoValue {
    override fun toString(): String = debugName
}

open class PseudoValueFactoryImpl : PseudoValueFactory {
    private var lastIndex: Int = 0

    override fun newValue(element: KtElement?, instruction: InstructionWithValue?): PseudoValue {
        return PseudoValueImpl((instruction?.let { "" } ?: "!") + "<v${lastIndex++}>", element, instruction)
    }
}
