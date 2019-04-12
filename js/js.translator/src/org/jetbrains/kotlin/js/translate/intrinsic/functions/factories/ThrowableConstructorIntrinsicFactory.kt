/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.intrinsic.functions.factories

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.backend.ast.JsExpression
import org.jetbrains.kotlin.js.backend.ast.JsInvocation
import org.jetbrains.kotlin.js.translate.callTranslator.CallInfo
import org.jetbrains.kotlin.js.translate.context.Namer
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.intrinsic.functions.basic.FunctionIntrinsic
import org.jetbrains.kotlin.js.translate.utils.JsAstUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.typeUtil.isNotNullThrowable

object ThrowableConstructorIntrinsicFactory : FunctionIntrinsicFactory {
    override fun getIntrinsic(descriptor: FunctionDescriptor, context: TranslationContext): FunctionIntrinsic? {
        if (descriptor !is ConstructorDescriptor) return null
        if (!descriptor.constructedClass.defaultType.isNotNullThrowable()) return null

        return Intrinsic
    }

    object Intrinsic : FunctionIntrinsic() {
        override fun apply(callInfo: CallInfo, arguments: List<JsExpression>, context: TranslationContext): JsExpression {
            val constructor = callInfo.resolvedCall.resultingDescriptor
            val argumentsToPass = arguments.toMutableList()
            val hasCauseParameter = constructor.valueParameters.any {
                it.type.constructor.declarationDescriptor?.fqNameSafe == KotlinBuiltIns.FQ_NAMES.throwable
            }

            if (constructor.valueParameters.size == 1 && hasCauseParameter) {
                argumentsToPass.add(0, Namer.getUndefinedExpression())
            }

            return JsInvocation(JsAstUtils.pureFqn("newThrowable", Namer.kotlinObject()), argumentsToPass)
        }
    }
}