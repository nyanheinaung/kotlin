/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.context

import org.jetbrains.kotlin.js.backend.ast.JsExpression
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor

class DeferredCallSite(
        val constructor: ConstructorDescriptor,
        val invocationArgs: MutableList<JsExpression>,
        val context: TranslationContext
)
