/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.analyzer

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.ErrorUtils

class JsAnalysisResult(
        val bindingTrace: BindingTrace,
        moduleDescriptor: ModuleDescriptor
) : AnalysisResult(bindingTrace.bindingContext, moduleDescriptor) {

    companion object {
        @JvmStatic fun success(trace: BindingTrace, module: ModuleDescriptor): JsAnalysisResult {
            return JsAnalysisResult(trace, module)
        }
    }
}
