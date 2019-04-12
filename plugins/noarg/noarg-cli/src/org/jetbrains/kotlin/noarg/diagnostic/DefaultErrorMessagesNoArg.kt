/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.noarg.diagnostic

import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

object DefaultErrorMessagesNoArg : DefaultErrorMessages.Extension {
    private val MAP = DiagnosticFactoryToRendererMap("AnnotationProcessing")
    override fun getMap() = MAP

    init {
        MAP.put(ErrorsNoArg.NO_NOARG_CONSTRUCTOR_IN_SUPERCLASS, "Zero-argument constructor was not found in the superclass")
    }
}