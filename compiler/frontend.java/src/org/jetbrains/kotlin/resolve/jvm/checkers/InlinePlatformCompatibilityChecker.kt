/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassOrPackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.kotlin.FileBasedKotlinClass
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinaryPackageSourceElement
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinarySourceElement
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor

class InlinePlatformCompatibilityChecker(val jvmTarget: JvmTarget, languageVersionSettings: LanguageVersionSettings) : CallChecker {

    private val properError = languageVersionSettings.supportsFeature(LanguageFeature.ProperInlineFromHigherPlatformDiagnostic)

    private val doCheck = doCheck()

    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (!doCheck) return

        val resultingDescriptor = resolvedCall.resultingDescriptor as? CallableMemberDescriptor ?: return
        if (!InlineUtil.isInline(resultingDescriptor)) {
            if (resultingDescriptor is PropertyDescriptor && InlineUtil.isInline(resultingDescriptor.getter)) {
                //TODO: we should distinguish setter usage from getter one, now we could report wrong diagnostic on non-inline setter
                //var prop: Int
                // inline get
                // set
                //
                // prop  - resolved call with property descriptor and we should report error
                // prop = 1 - resolved call with setter for whole expression and property descriptor for left part,
                //              so we couldn't distinguish is this expression for setter or for getter and will report wrong diagnostic
            } else {
                return
            }
        }

        val propertyOrFun = DescriptorUtils.getDirectMember(resultingDescriptor)

        val compilingBytecodeVersion = jvmTarget.bytecodeVersion
        if (!properError) {
            val inliningBytecodeVersion = getBytecodeVersionIfDeserializedDescriptor(propertyOrFun, false)
            if (inliningBytecodeVersion != null && compilingBytecodeVersion < inliningBytecodeVersion) {
                context.trace.report(
                    ErrorsJvm.INLINE_FROM_HIGHER_PLATFORM.on(
                        reportOn,
                        JvmTarget.getDescription(inliningBytecodeVersion),
                        JvmTarget.getDescription(compilingBytecodeVersion)
                    )
                )
                return
            }
        }

        val inliningBytecodeVersionProper = getBytecodeVersionIfDeserializedDescriptor(propertyOrFun, true) ?: return

        if (compilingBytecodeVersion < inliningBytecodeVersionProper) {
            if (properError) {
                context.trace.report(
                    ErrorsJvm.INLINE_FROM_HIGHER_PLATFORM.on(
                        reportOn,
                        JvmTarget.getDescription(inliningBytecodeVersionProper),
                        JvmTarget.getDescription(compilingBytecodeVersion)
                    )
                )
            } else {
                //INLINE_FROM_HIGHER_PLATFORM was checked in `if (!properError)`
                context.trace.report(
                    ErrorsJvm.INLINE_FROM_HIGHER_PLATFORM_WARNING.on(
                        reportOn,
                        JvmTarget.getDescription(inliningBytecodeVersionProper),
                        JvmTarget.getDescription(compilingBytecodeVersion)
                    )
                )
            }
        }

    }

    companion object {
        fun doCheck() = "true" != System.getProperty("kotlin.skip.bytecode.version.check")

        fun getBytecodeVersionIfDeserializedDescriptor(
            funOrProperty: DeclarationDescriptor,
            useConcreteSuperImplementation: Boolean
        ): Int? {
            if (funOrProperty !is DeserializedCallableMemberDescriptor) return null

            val realDeclarationIfFound =
                if (useConcreteSuperImplementation) funOrProperty.getConcreteDeclarationForInline() else funOrProperty
            val containingDeclaration = realDeclarationIfFound.containingDeclaration as ClassOrPackageFragmentDescriptor

            val source = containingDeclaration.source
            val binaryClass =
                when (source) {
                    is KotlinJvmBinarySourceElement -> source.binaryClass
                    is KotlinJvmBinaryPackageSourceElement -> source.getContainingBinaryClass(funOrProperty)
                    else -> null
                } as? FileBasedKotlinClass ?: return null

            return binaryClass.classVersion
        }

        private fun CallableMemberDescriptor.getConcreteDeclarationForInline(): CallableMemberDescriptor {
            if (!this.kind.isReal) {
                val superImplementation = overriddenDescriptors.firstOrNull {
                    val containingDeclaration = it.containingDeclaration
                    !DescriptorUtils.isInterface(containingDeclaration) && !DescriptorUtils.isAnnotationClass(containingDeclaration)

                }
                superImplementation?.let {
                    return it.getConcreteDeclarationForInline()
                }
            }
            return this
        }
    }
}
