/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.common

import org.jetbrains.org.objectweb.asm.tree.MethodNode
import org.jetbrains.org.objectweb.asm.tree.analysis.Frame
import org.jetbrains.org.objectweb.asm.tree.analysis.Interpreter
import org.jetbrains.org.objectweb.asm.tree.analysis.Value

class CustomFramesMethodAnalyzer<V : Value>(
    owner: String, method: MethodNode, interpreter: Interpreter<V>,
    private val frameFactory: (Int, Int) -> Frame<V>
) : MethodAnalyzer<V>(owner, method, interpreter) {
    override fun newFrame(nLocals: Int, nStack: Int) = frameFactory(nLocals, nStack)
}
