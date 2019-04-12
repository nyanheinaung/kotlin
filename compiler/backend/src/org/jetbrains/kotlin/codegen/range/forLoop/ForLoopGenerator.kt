/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.org.objectweb.asm.Label

interface ForLoopGenerator {
    val forExpression: KtForExpression
    fun beforeLoop()
    fun checkEmptyLoop(loopExit: Label)
    fun checkPreCondition(loopExit: Label)
    fun beforeBody()
    fun body()
    fun afterBody(loopExit: Label)
    fun afterLoop()
}