/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.intrinsic.functions.basic

import org.jetbrains.kotlin.js.backend.ast.JsExpression
import org.jetbrains.kotlin.js.translate.callTranslator.CallInfo
import org.jetbrains.kotlin.js.translate.context.TranslationContext

/**
 * Base for intrinsics that substitute standard function calls like Int#plus, Float#minus ... etc
 */
abstract class FunctionIntrinsic {
    abstract fun apply(callInfo: CallInfo, arguments: List<JsExpression>, context: TranslationContext): JsExpression
}
