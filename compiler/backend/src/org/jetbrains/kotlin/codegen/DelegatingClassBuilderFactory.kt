/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.intellij.psi.PsiElement
import com.intellij.util.containers.MultiMap
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ConflictingJvmDeclarationsData
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.MemberKind
import org.jetbrains.kotlin.resolve.jvm.diagnostics.RawSignature
import org.jetbrains.org.objectweb.asm.FieldVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

abstract class DelegatingClassBuilderFactory(
        protected val delegate: ClassBuilderFactory

) : ClassBuilderFactory by delegate {

    abstract override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder

    override fun asBytes(builder: ClassBuilder?): ByteArray? {
        return delegate.asBytes((builder as DelegatingClassBuilder).delegate)
    }

    override fun asText(builder: ClassBuilder?): String? {
        return delegate.asText((builder as DelegatingClassBuilder).delegate)
    }
}
