/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers

import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.hasError
import org.jetbrains.kotlin.js.analyzer.JsAnalysisResult
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.js.facade.K2JSTranslator
import org.jetbrains.kotlin.js.facade.MainCallParameters
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace

abstract class AbstractDiagnosticsTestWithJsStdLibAndBackendCompilation : AbstractDiagnosticsTestWithJsStdLib() {
    override fun analyzeModuleContents(
            moduleContext: ModuleContext,
            files: List<KtFile>,
            moduleTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings,
            separateModules: Boolean,
            jvmTarget: JvmTarget
    ): JsAnalysisResult {
        val analysisResult = super.analyzeModuleContents(moduleContext, files, moduleTrace, languageVersionSettings, separateModules, jvmTarget)
        val diagnostics = analysisResult.bindingTrace.bindingContext.diagnostics

        if (!hasError(diagnostics)) {
            val translator = K2JSTranslator(config, false)
            translator.translate(object : JsConfig.Reporter() {}, files, MainCallParameters.noCall(), analysisResult)
        }

        return analysisResult
    }
}
