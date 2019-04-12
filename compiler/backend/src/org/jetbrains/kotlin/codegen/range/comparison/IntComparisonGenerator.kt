/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.comparison

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class IntegerComparisonGenerator(override val comparedType: Type) : SignedIntegerComparisonGenerator {
    override fun jumpIfGreaterOrEqual(v: InstructionAdapter, label: Label) {
        v.ificmpge(label)
    }

    override fun jumpIfLessOrEqual(v: InstructionAdapter, label: Label) {
        v.ificmple(label)
    }

    override fun jumpIfGreater(v: InstructionAdapter, label: Label) {
        v.ificmpgt(label)
    }

    override fun jumpIfLess(v: InstructionAdapter, label: Label) {
        v.ificmplt(label)
    }

    override fun jumpIfLessThanZero(v: InstructionAdapter, label: Label) {
        v.iflt(label)
    }
}

val IntComparisonGenerator = IntegerComparisonGenerator(Type.INT_TYPE)
val CharComparisonGenerator = IntegerComparisonGenerator(Type.CHAR_TYPE)