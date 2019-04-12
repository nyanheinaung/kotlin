/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.ClassLiteralChecker

object JsModuleClassLiteralChecker : ClassLiteralChecker {
    override fun check(expression: KtClassLiteralExpression, type: KotlinType, context: ResolutionContext<*>) {
        val descriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return
        checkJsModuleUsage(context.trace.bindingContext, context.trace, context.scope.ownerDescriptor, descriptor, expression)
    }
}
