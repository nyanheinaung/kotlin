/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.calls.checkers.AdditionalTypeChecker
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.types.KotlinType


class JavaTypeAccessibilityChecker : AdditionalTypeChecker {
    override fun checkType(
            expression: KtExpression,
            expressionType: KotlinType,
            expressionTypeWithSmartCast: KotlinType,
            c: ResolutionContext<*>
    ) {
        // NB in Kotlin class hierarchy leading to "pathological" type inference is impossible
        // due to EXPOSED_SUPER_CLASS & EXPOSED_SUPER_INTERFACE checks.
        // To avoid superfluous diagnostics in case of invisible member class and so on,
        // we consider only Java classes as possibly inaccessible.

        if (c.isDebuggerContext) return

        val inaccessibleClasses = findInaccessibleJavaClasses(expressionType, c)
        if (inaccessibleClasses.isNotEmpty()) {
            c.trace.report(Errors.INACCESSIBLE_TYPE.on(expression, expressionType, inaccessibleClasses))
            return
        }

        if (expressionTypeWithSmartCast != expressionType) {
            val inaccessibleClassesWithSmartCast = findInaccessibleJavaClasses(expressionTypeWithSmartCast, c)
            if (inaccessibleClassesWithSmartCast.isNotEmpty()) {
                c.trace.report(Errors.INACCESSIBLE_TYPE.on(expression, expressionType, inaccessibleClassesWithSmartCast))
            }
        }
    }

    private fun findInaccessibleJavaClasses(type: KotlinType, c: ResolutionContext<*>): Collection<ClassDescriptor> {
        val scopeOwner = c.scope.ownerDescriptor
        val inaccessibleJavaClasses = LinkedHashSet<ClassDescriptor>()
        findInaccessibleJavaClassesRec(type, scopeOwner, inaccessibleJavaClasses)
        return inaccessibleJavaClasses
    }

    private fun findInaccessibleJavaClassesRec(
            type: KotlinType,
            scopeOwner: DeclarationDescriptor,
            inaccessibleClasses: MutableCollection<ClassDescriptor>
    ) {
        val declarationDescriptor = type.constructor.declarationDescriptor

        if (declarationDescriptor is JavaClassDescriptor) {
            if (!Visibilities.isVisibleIgnoringReceiver(declarationDescriptor, scopeOwner)) {
                inaccessibleClasses.add(declarationDescriptor)
            }
        }

        for (typeProjection in type.arguments) {
            if (typeProjection.isStarProjection) continue
            findInaccessibleJavaClassesRec(typeProjection.type, scopeOwner, inaccessibleClasses)
        }
    }

}