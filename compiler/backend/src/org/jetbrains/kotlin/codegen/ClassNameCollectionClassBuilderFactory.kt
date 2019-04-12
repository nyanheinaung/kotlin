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

abstract class ClassNameCollectionClassBuilderFactory(
        delegate: ClassBuilderFactory
) : DelegatingClassBuilderFactory(delegate) {

    protected abstract fun handleClashingNames(internalName: String, origin: JvmDeclarationOrigin)

    override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder {
        return ClassNameCollectionClassBuilder(origin, delegate.newClassBuilder(origin))
    }

    private inner class ClassNameCollectionClassBuilder(
            private val classCreatedFor: JvmDeclarationOrigin,
            internal val _delegate: ClassBuilder
    ) : DelegatingClassBuilder() {

        override fun getDelegate() = _delegate

        private lateinit var classInternalName: String

        override fun defineClass(origin: PsiElement?, version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<out String>) {
            classInternalName = name
            super.defineClass(origin, version, access, name, signature, superName, interfaces)
        }

        override fun done() {
            handleClashingNames(classInternalName, classCreatedFor)
            super.done()
        }
    }
}
