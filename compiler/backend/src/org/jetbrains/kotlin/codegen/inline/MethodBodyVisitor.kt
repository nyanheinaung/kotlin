/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.*
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

open class SkipMaxAndEndVisitor(mv: MethodVisitor) : InstructionAdapter(Opcodes.API_VERSION, mv) {
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {}

    override fun visitEnd() {}
}

open class MethodBodyVisitor(mv: MethodVisitor) : MethodVisitor(Opcodes.API_VERSION, mv) {

    @Suppress("NOTHING_TO_OVERRIDE")
    override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {
    }

    override fun visitParameter(name: String, access: Int) {}

    override fun visitAnnotationDefault(): AnnotationVisitor? = null

    override fun visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor? = null

    override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath, desc: String, visible: Boolean): AnnotationVisitor? = null

    override fun visitParameterAnnotation(parameter: Int, desc: String, visible: Boolean): AnnotationVisitor? = null

    override fun visitAttribute(attr: Attribute) {}

    override fun visitCode() {}

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {}

    override fun visitEnd() {}
}
