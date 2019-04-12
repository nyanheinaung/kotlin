/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.comparison

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

object ObjectComparisonGenerator : ComparisonGenerator {
    override val comparedType: Type = Type.getObjectType("java/lang/Comparable")

    override fun jumpIfGreaterOrEqual(v: InstructionAdapter, label: Label) {
        invokeCompare(v)
        v.ifge(label)
    }

    override fun jumpIfLessOrEqual(v: InstructionAdapter, label: Label) {
        invokeCompare(v)
        v.ifle(label)
    }

    override fun jumpIfGreater(v: InstructionAdapter, label: Label) {
        invokeCompare(v)
        v.ifgt(label)
    }

    override fun jumpIfLess(v: InstructionAdapter, label: Label) {
        invokeCompare(v)
        v.iflt(label)
    }

    private fun invokeCompare(v: InstructionAdapter) {
        v.invokeinterface("java/lang/Comparable", "compareTo", "(Ljava/lang/Object;)I")
    }
}