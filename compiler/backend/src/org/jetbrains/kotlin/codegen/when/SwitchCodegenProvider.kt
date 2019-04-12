/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.`when`

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.binding.CodegenBinding
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.constants.*
import org.jetbrains.org.objectweb.asm.Type

import java.util.ArrayList

class SwitchCodegenProvider
private constructor(
    private val bindingContext: BindingContext,
    private val shouldInlineConstVals: Boolean,
    private val codegen: ExpressionCodegen?
) {
    constructor(state: GenerationState) : this(state.bindingContext, state.shouldInlineConstVals, null)
    constructor(codegen: ExpressionCodegen) : this(codegen.bindingContext, codegen.state.shouldInlineConstVals, codegen)

    fun checkAllItemsAreConstantsSatisfying(expression: KtWhenExpression, predicate: Function1<ConstantValue<*>, Boolean>): Boolean =
        expression.entries.all { entry ->
            entry.conditions.all { condition ->
                if (condition !is KtWhenConditionWithExpression) return false
                val patternExpression = condition.expression ?: return false
                val constant =
                    ExpressionCodegen.getCompileTimeConstant(patternExpression, bindingContext, shouldInlineConstVals) ?: return false
                predicate.invoke(constant)
            }
        }

    fun getAllConstants(expression: KtWhenExpression): Iterable<ConstantValue<*>?> =
        ArrayList<ConstantValue<*>?>().apply {
            for (entry in expression.entries) {
                addConstantsFromConditions(entry)
            }
        }

    fun getConstantsFromEntry(entry: KtWhenEntry): Iterable<ConstantValue<*>?> =
        ArrayList<ConstantValue<*>?>().apply {
            addConstantsFromConditions(entry)
        }

    private fun ArrayList<ConstantValue<*>?>.addConstantsFromConditions(entry: KtWhenEntry) {
        for (condition in entry.conditions) {
            if (condition !is KtWhenConditionWithExpression) continue
            val patternExpression = condition.expression ?: throw AssertionError("expression in when should not be null")
            add(ExpressionCodegen.getCompileTimeConstant(patternExpression, bindingContext, shouldInlineConstVals))
        }
    }

    fun buildAppropriateSwitchCodegenIfPossible(
        expression: KtWhenExpression,
        isStatement: Boolean,
        isExhaustive: Boolean
    ): SwitchCodegen? {
        val codegen = codegen ?: throw AssertionError("Can't create SwitchCodegen in this context")

        if (!isThereConstantEntriesButNulls(expression)) {
            return null
        }

        val subjectType = codegen.expressionType(expression.subjectExpression)

        val mapping = codegen.bindingContext.get(CodegenBinding.MAPPING_FOR_WHEN_BY_ENUM, expression)

        return when {
            mapping != null ->
                EnumSwitchCodegen(expression, isStatement, isExhaustive, codegen, mapping)
            isIntegralConstantsSwitch(expression, subjectType) ->
                IntegralConstantsSwitchCodegen(expression, isStatement, isExhaustive, codegen)
            isStringConstantsSwitch(expression, subjectType) ->
                StringSwitchCodegen(expression, isStatement, isExhaustive, codegen)
            else -> null
        }

    }

    private fun isThereConstantEntriesButNulls(expression: KtWhenExpression): Boolean =
        getAllConstants(expression).any { it != null && it !is NullValue }

    private fun isIntegralConstantsSwitch(expression: KtWhenExpression, subjectType: Type): Boolean =
        AsmUtil.isIntPrimitive(subjectType) &&
                checkAllItemsAreConstantsSatisfying(expression) { it is IntegerValueConstant<*> || it is UnsignedValueConstant<*> }

    private fun isStringConstantsSwitch(expression: KtWhenExpression, subjectType: Type): Boolean =
        subjectType.className == String::class.java.name &&
                checkAllItemsAreConstantsSatisfying(expression) { it is StringValue || it is NullValue }
}
