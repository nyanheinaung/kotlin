/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.MethodNode

class DeferredMethodVisitor(
    val intermediate: MethodNode,
    private val resultNode: () -> MethodVisitor
) : MethodVisitor(Opcodes.API_VERSION, intermediate) {

    override fun visitEnd() {
        super.visitEnd()
        val resultVisitor = resultNode()
        intermediate.accept(resultVisitor)
    }
}