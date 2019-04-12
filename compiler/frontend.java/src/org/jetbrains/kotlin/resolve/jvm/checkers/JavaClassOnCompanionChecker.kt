/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl

class JavaClassOnCompanionChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val descriptor = resolvedCall.resultingDescriptor
        if (descriptor !is PropertyDescriptor || descriptor.name.asString() != "javaClass") return

        val container = descriptor.containingDeclaration
        if (container !is PackageFragmentDescriptor || container.fqName.asString() != "kotlin.jvm") return

        val actualType = descriptor.type

        val companionObject = actualType.arguments.singleOrNull()?.type?.constructor?.declarationDescriptor as? ClassDescriptor ?: return
        if (companionObject.isCompanionObject) {
            val containingClass = companionObject.containingDeclaration as ClassDescriptor
            val javaLangClass = actualType.constructor.declarationDescriptor as? ClassDescriptor ?: return

            val arguments = listOf(TypeProjectionImpl(containingClass.defaultType))
            val expectedType = KotlinTypeFactory.simpleType(Annotations.EMPTY, javaLangClass.typeConstructor, arguments,
                                                            actualType.isMarkedNullable)
            context.trace.report(ErrorsJvm.JAVA_CLASS_ON_COMPANION.on(reportOn, actualType, expectedType))
        }
    }
}
