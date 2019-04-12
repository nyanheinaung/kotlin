/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.comparison

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class FloatingPointComparisonGenerator(override val comparedType: Type) : ComparisonGenerator {
    override fun jumpIfGreaterOrEqual(v: InstructionAdapter, label: Label) {
        v.cmpg(comparedType)
        v.ifge(label)
    }

    override fun jumpIfLessOrEqual(v: InstructionAdapter, label: Label) {
        v.cmpl(comparedType)
        v.ifle(label)
    }

    override fun jumpIfGreater(v: InstructionAdapter, label: Label) {
        v.cmpg(comparedType)
        v.ifgt(label)
    }

    override fun jumpIfLess(v: InstructionAdapter, label: Label) {
        v.cmpl(comparedType)
        v.iflt(label)
    }
}

val FloatComparisonGenerator = FloatingPointComparisonGenerator(Type.FLOAT_TYPE)
val DoubleComparisonGenerator = FloatingPointComparisonGenerator(Type.DOUBLE_TYPE)