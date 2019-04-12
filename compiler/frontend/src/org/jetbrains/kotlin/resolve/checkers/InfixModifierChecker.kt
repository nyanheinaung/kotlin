/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.util.CheckResult
import org.jetbrains.kotlin.util.InfixChecks

class InfixModifierChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        val functionDescriptor = descriptor as? FunctionDescriptor ?: return
        if (!functionDescriptor.isInfix) return
        val modifier = declaration.modifierList?.getModifier(KtTokens.INFIX_KEYWORD) ?: return

        val checkResult = InfixChecks.check(functionDescriptor)
        if (checkResult !is CheckResult.IllegalSignature) return

        context.trace.report(Errors.INAPPLICABLE_INFIX_MODIFIER.on(modifier, checkResult.error))
    }
}
