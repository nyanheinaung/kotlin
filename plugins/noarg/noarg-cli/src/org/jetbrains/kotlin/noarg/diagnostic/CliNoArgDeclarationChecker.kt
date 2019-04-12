/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.diagnostic

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.reportFromPlugin
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.noarg.NO_ARG_CLASS_KEY
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny

class CliNoArgDeclarationChecker(val noArgAnnotationFqNames: List<String>) : AbstractNoArgDeclarationChecker() {
    override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?) = noArgAnnotationFqNames
}

abstract class AbstractNoArgDeclarationChecker : DeclarationChecker, AnnotationBasedExtension {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        // Handle only classes
        if (descriptor !is ClassDescriptor || declaration !is KtClass) return
        if (descriptor.kind != ClassKind.CLASS) return

        val hasSpecialAnnotation = descriptor.hasSpecialAnnotation(declaration)
        declaration.putUserData(NO_ARG_CLASS_KEY, hasSpecialAnnotation)
        if (hasSpecialAnnotation) {
            val superClass = descriptor.getSuperClassOrAny()
            if (superClass.constructors.none { it.isNoArgConstructor() } && !superClass.hasSpecialAnnotation(declaration)) {
                val reportTarget = declaration.nameIdentifier ?: declaration.getClassOrInterfaceKeyword() ?: declaration
                context.trace.reportFromPlugin(ErrorsNoArg.NO_NOARG_CONSTRUCTOR_IN_SUPERCLASS.on(reportTarget), DefaultErrorMessagesNoArg)
            }
        }
    }

    private fun ConstructorDescriptor.isNoArgConstructor() = valueParameters.all(ValueParameterDescriptor::declaresDefaultValue)
}
