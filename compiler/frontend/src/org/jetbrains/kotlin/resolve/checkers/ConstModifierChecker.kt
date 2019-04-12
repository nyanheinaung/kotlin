/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.isError

object ConstModifierChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is VariableDescriptor || !declaration.hasModifier(KtTokens.CONST_KEYWORD)) return

        val constModifierPsiElement = declaration.modifierList!!.getModifier(KtTokens.CONST_KEYWORD)!!

        val diagnostic = checkCanBeConst(declaration, constModifierPsiElement, descriptor).diagnostic
        if (diagnostic != null) {
            context.trace.report(diagnostic)
        }
    }

    fun canBeConst(declaration: KtDeclaration, constModifierPsiElement: PsiElement, descriptor: VariableDescriptor): Boolean =
        checkCanBeConst(declaration, constModifierPsiElement, descriptor).canBeConst

    private fun checkCanBeConst(
        declaration: KtDeclaration,
        constModifierPsiElement: PsiElement,
        descriptor: VariableDescriptor
    ): ConstApplicability {
        if (descriptor.isVar) {
            return Errors.WRONG_MODIFIER_TARGET.on(constModifierPsiElement, KtTokens.CONST_KEYWORD, "vars").nonApplicable()
        }

        val containingDeclaration = descriptor.containingDeclaration
        if (containingDeclaration is ClassDescriptor && containingDeclaration.kind != ClassKind.OBJECT) {
            return Errors.CONST_VAL_NOT_TOP_LEVEL_OR_OBJECT.on(constModifierPsiElement).nonApplicable()
        }

        if (declaration !is KtProperty || descriptor !is PropertyDescriptor) return ConstApplicability.NonApplicable()

        if (declaration.hasDelegate()) {
            return Errors.CONST_VAL_WITH_DELEGATE.on(declaration.delegate!!).nonApplicable()
        }

        val getter = declaration.getter
        if (!descriptor.getter!!.isDefault && getter != null) {
            return Errors.CONST_VAL_WITH_GETTER.on(getter).nonApplicable()
        }

        if (descriptor.type.isError) return ConstApplicability.NonApplicable()

        // Report errors about const initializer only on property of resolved types
        if (!descriptor.type.canBeUsedForConstVal()) {
            return Errors.TYPE_CANT_BE_USED_FOR_CONST_VAL.on(constModifierPsiElement, descriptor.type).nonApplicable()
        }

        if (declaration.initializer == null) {
            return Errors.CONST_VAL_WITHOUT_INITIALIZER.on(constModifierPsiElement).nonApplicable()
        }

        if (descriptor.compileTimeInitializer == null) {
            return Errors.CONST_VAL_WITH_NON_CONST_INITIALIZER.on(declaration.initializer!!).nonApplicable()
        }

        return ConstApplicability.Applicable
    }
}

sealed class ConstApplicability(val canBeConst: Boolean, val diagnostic: Diagnostic?) {
    object Applicable : ConstApplicability(true, null)
    class NonApplicable(diagnostic: Diagnostic? = null) : ConstApplicability(false, diagnostic)
}

private fun Diagnostic.nonApplicable() = ConstApplicability.NonApplicable(this)
