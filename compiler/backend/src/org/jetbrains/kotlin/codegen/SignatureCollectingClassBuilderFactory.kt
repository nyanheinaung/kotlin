/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.intellij.psi.PsiElement
import com.intellij.util.containers.LinkedMultiMap
import com.intellij.util.containers.MultiMap
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ConflictingJvmDeclarationsData
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.MemberKind
import org.jetbrains.kotlin.resolve.jvm.diagnostics.RawSignature
import org.jetbrains.org.objectweb.asm.FieldVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

abstract class SignatureCollectingClassBuilderFactory(
        delegate: ClassBuilderFactory, val shouldGenerate: (JvmDeclarationOrigin) -> Boolean
) : DelegatingClassBuilderFactory(delegate) {

    protected abstract fun handleClashingSignatures(data: ConflictingJvmDeclarationsData)
    protected abstract fun onClassDone(classOrigin: JvmDeclarationOrigin,
                                       classInternalName: String,
                                       signatures: MultiMap<RawSignature, JvmDeclarationOrigin>)

    override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder {
        return SignatureCollectingClassBuilder(origin, delegate.newClassBuilder(origin))
    }

    private inner class SignatureCollectingClassBuilder(
            private val classCreatedFor: JvmDeclarationOrigin,
            internal val _delegate: ClassBuilder
    ) : DelegatingClassBuilder() {

        override fun getDelegate() = _delegate

        private lateinit var classInternalName: String

        private val signatures = LinkedMultiMap<RawSignature, JvmDeclarationOrigin>()

        override fun defineClass(origin: PsiElement?, version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<out String>) {
            classInternalName = name
            super.defineClass(origin, version, access, name, signature, superName, interfaces)
        }

        override fun newField(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, value: Any?): FieldVisitor {
            signatures.putValue(RawSignature(name, desc, MemberKind.FIELD), origin)
            if (!shouldGenerate(origin)) {
                return AbstractClassBuilder.EMPTY_FIELD_VISITOR
            }
            return super.newField(origin, access, name, desc, signature, value)
        }

        override fun newMethod(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
            signatures.putValue(RawSignature(name, desc, MemberKind.METHOD), origin)
            if (!shouldGenerate(origin)) {
                return AbstractClassBuilder.EMPTY_METHOD_VISITOR
            }
            return super.newMethod(origin, access, name, desc, signature, exceptions)
        }

        override fun done() {
            for ((signature, elementsAndDescriptors) in signatures.entrySet()) {
                if (elementsAndDescriptors.size == 1) continue // no clash
                handleClashingSignatures(ConflictingJvmDeclarationsData(
                        classInternalName,
                        classCreatedFor,
                        signature,
                        elementsAndDescriptors
                ))
            }
            onClassDone(classCreatedFor, classInternalName, signatures)
            super.done()
        }

    }
}
