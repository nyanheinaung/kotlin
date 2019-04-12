/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.parsing

import org.jetbrains.kotlin.contracts.description.expressions.BooleanConstantReference
import org.jetbrains.kotlin.contracts.description.expressions.ConstantReference
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtVisitor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.types.KotlinType

internal class PsiConstantParser(private val callContext: ContractCallContext) : KtVisitor<ConstantReference?, Unit>() {
    override fun visitKtElement(element: KtElement, data: Unit?): ConstantReference? = null

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): ConstantReference? {
        val type: KotlinType = callContext.bindingContext.getType(expression) ?: return null

        val compileTimeConstant: CompileTimeConstant<*> = callContext.bindingContext.get(BindingContext.COMPILE_TIME_VALUE, expression)
            ?: return null

        val value: Any? = compileTimeConstant.getValue(type)

        return when (value) {
            true -> BooleanConstantReference.TRUE
            false -> BooleanConstantReference.FALSE
            null -> ConstantReference.NULL
            else -> null
        }
    }
}