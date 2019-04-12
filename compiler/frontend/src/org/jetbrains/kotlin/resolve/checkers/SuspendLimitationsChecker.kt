/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.abbreviationFqName
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.getAbbreviation
import org.jetbrains.kotlin.util.OperatorNameConventions

object SuspendLimitationsChecker : DeclarationChecker {
    private val UNSUPPORTED_OPERATOR_NAMES = setOf(
        OperatorNameConventions.CONTAINS,
        OperatorNameConventions.GET, OperatorNameConventions.SET,
        OperatorNameConventions.PROVIDE_DELEGATE, OperatorNameConventions.GET_VALUE, OperatorNameConventions.SET_VALUE
    )

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is FunctionDescriptor || !descriptor.isSuspend) return

        if (descriptor.isOperator && descriptor.name in UNSUPPORTED_OPERATOR_NAMES) {
            declaration.modifierList?.getModifier(KtTokens.OPERATOR_KEYWORD)?.let {
                context.trace.report(Errors.UNSUPPORTED.on(it, "suspend operator \"${descriptor.name}\""))
            }
        }

        if (descriptor.annotations.any(AnnotationDescriptor::isKotlinTestAnnotation)) {
            declaration.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD)?.let {
                context.trace.report(Errors.UNSUPPORTED.on(it, "suspend test functions"))
            }
        }
    }
}

private val KOTLIN_TEST_TEST_FQNAME = FqName("kotlin.test.Test")
private fun AnnotationDescriptor.isKotlinTestAnnotation() =
    fqName == KOTLIN_TEST_TEST_FQNAME || abbreviationFqName == KOTLIN_TEST_TEST_FQNAME
