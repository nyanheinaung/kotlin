/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorResolver
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator
import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isError

class ValueParameterResolver(
    private val expressionTypingServices: ExpressionTypingServices,
    private val constantExpressionEvaluator: ConstantExpressionEvaluator,
    private val languageVersionSettings: LanguageVersionSettings,
    private val dataFlowValueFactory: DataFlowValueFactory
) {
    fun resolveValueParameters(
        valueParameters: List<KtParameter>,
        valueParameterDescriptors: List<ValueParameterDescriptor>,
        declaringScope: LexicalScope,
        dataFlowInfo: DataFlowInfo,
        trace: BindingTrace
    ) {
        val scopeForDefaultValue =
            LexicalScopeImpl(declaringScope, declaringScope.ownerDescriptor, false, null, LexicalScopeKind.DEFAULT_VALUE)

        val contextForDefaultValue = ExpressionTypingContext.newContext(
            trace, scopeForDefaultValue, dataFlowInfo, TypeUtils.NO_EXPECTED_TYPE,
            languageVersionSettings, dataFlowValueFactory
        )

        for ((descriptor, parameter) in valueParameterDescriptors.zip(valueParameters)) {
            ForceResolveUtil.forceResolveAllContents(descriptor.annotations)
            resolveDefaultValue(descriptor, parameter, contextForDefaultValue)
        }
    }

    private fun resolveDefaultValue(
        valueParameterDescriptor: ValueParameterDescriptor,
        parameter: KtParameter,
        context: ExpressionTypingContext
    ) {
        if (!valueParameterDescriptor.declaresDefaultValue()) return
        val defaultValue = parameter.defaultValue ?: return
        val type = valueParameterDescriptor.type
        expressionTypingServices.getTypeInfo(defaultValue, context.replaceExpectedType(type))
        if (DescriptorUtils.isAnnotationClass(DescriptorResolver.getContainingClass(context.scope))) {
            val constant = constantExpressionEvaluator.evaluateExpression(defaultValue, context.trace, type)
            if ((constant == null || constant.usesNonConstValAsConstant) && !type.isError) {
                context.trace.report(Errors.ANNOTATION_PARAMETER_DEFAULT_VALUE_MUST_BE_CONSTANT.on(defaultValue))
            }
        }
    }
}
