/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.naming.NameSuggestion
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.tasks.isDynamic
import org.jetbrains.kotlin.types.expressions.OperatorConventions
import org.jetbrains.kotlin.types.isDynamic

object JsDynamicCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        val callee = resolvedCall.resultingDescriptor
        if (!callee.isDynamic()) {
            return checkSpreadOperator(resolvedCall, context)
        }

        val element = resolvedCall.call.callElement
        when (element) {
            is KtArrayAccessExpression -> {
                if (element.indexExpressions.size > 1) {
                    context.trace.report(ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC.on(reportOn, "indexed access with more than one index"))
                }
            }

            is KtWhenConditionInRange -> {
                reportInOperation(context, reportOn)
            }

            is KtBinaryExpression -> {
                val token = element.operationToken
                when (token) {
                    in OperatorConventions.IN_OPERATIONS -> {
                        reportInOperation(context, reportOn)
                    }
                    KtTokens.RANGE -> {
                        context.trace.report(ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC.on(reportOn, "`..` operation"))
                    }
                }
            }

            is KtDestructuringDeclarationEntry -> {
                if (!reportedOn(context, element.node.treeParent.psi)) {
                    context.trace.report(ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC.on(element.parent, "destructuring declaration"))
                }
            }

            is KtSimpleNameExpression -> checkIdentifier(element.getReferencedName(), element, context)

            is KtCallExpression -> {
                val calleePsi = element.calleeExpression
                if (calleePsi is KtSimpleNameExpression) {
                    checkIdentifier(calleePsi.getReferencedName(), calleePsi, context)
                }
            }
        }

        for (argument in resolvedCall.call.valueArguments) {
            argument.getSpreadElement()?.let {
                context.trace.report(ErrorsJs.SPREAD_OPERATOR_IN_DYNAMIC_CALL.on(it))
            }
        }
    }

    private fun checkIdentifier(name: String?, reportOn: PsiElement, context: CallCheckerContext) {
        if (name == null) return
        if (NameSuggestion.sanitizeName(name) != name) {
            context.trace.report(ErrorsJs.NAME_CONTAINS_ILLEGAL_CHARS.on(reportOn))
        }
    }

    private fun checkSpreadOperator(resolvedCall: ResolvedCall<*>, context: CallCheckerContext) {
        for (arg in resolvedCall.call.valueArguments) {
            val argExpression = arg.getArgumentExpression() ?: continue
            if (context.trace.bindingContext.getType(argExpression)?.isDynamic() == true && arg.getSpreadElement() != null) {
                context.trace.report(ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC.on(arg.asElement(), "spread operator"))
            }
        }
    }

    private fun reportInOperation(context: CallCheckerContext, reportOn: PsiElement) {
        context.trace.report(ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC.on(reportOn, "`in` operation"))
    }

    private fun reportedOn(context: CallCheckerContext, element: PsiElement) =
            context.trace.bindingContext.diagnostics.forElement(element).any { it.factory == ErrorsJs.WRONG_OPERATION_WITH_DYNAMIC }
}