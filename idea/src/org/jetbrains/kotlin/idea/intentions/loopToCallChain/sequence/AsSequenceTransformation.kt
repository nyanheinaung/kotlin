/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.intentions.loopToCallChain.sequence

import org.jetbrains.kotlin.idea.intentions.loopToCallChain.ChainedCallGenerator
import org.jetbrains.kotlin.idea.intentions.loopToCallChain.SequenceTransformation
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

class AsSequenceTransformation(override val loop: KtForExpression) : SequenceTransformation {
    override val presentation: String
        get() = "asSequence()"

    override val affectsIndex: Boolean
        get() = false

    override fun generateCode(chainedCallGenerator: ChainedCallGenerator): KtExpression {
        return chainedCallGenerator.generate("asSequence()")
    }
}