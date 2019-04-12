/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization

import org.jetbrains.kotlin.codegen.optimization.transformer.MethodTransformer
import org.jetbrains.org.objectweb.asm.tree.MethodNode
import org.jetbrains.org.objectweb.asm.tree.analysis.BasicVerifier

class MethodVerifier(private val checkPoint: String) : MethodTransformer() {
    override fun transform(internalClassName: String, methodNode: MethodNode) {
        try {
            analyze(internalClassName, methodNode, BasicVerifier())
        } catch (e: Throwable) {
            throw AssertionError("$checkPoint: incorrect bytecode", e)
        }
    }
}
