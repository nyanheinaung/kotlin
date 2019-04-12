/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.comparison

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

object LongComparisonGenerator : SignedIntegerComparisonGenerator {
    override val comparedType: Type = Type.LONG_TYPE

    override fun jumpIfGreaterOrEqual(v: InstructionAdapter, label: Label) {
        v.lcmp()
        v.ifge(label)
    }

    override fun jumpIfLessOrEqual(v: InstructionAdapter, label: Label) {
        v.lcmp()
        v.ifle(label)
    }

    override fun jumpIfGreater(v: InstructionAdapter, label: Label) {
        v.lcmp()
        v.ifgt(label)
    }

    override fun jumpIfLess(v: InstructionAdapter, label: Label) {
        v.lcmp()
        v.iflt(label)
    }

    override fun jumpIfLessThanZero(v: InstructionAdapter, label: Label) {
        v.lconst(0L)
        v.lcmp()
        v.iflt(label)
    }
}