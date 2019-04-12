/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.experimental.host

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.api.*

/**
 * The base class for scripting host implementations
 */
abstract class BasicScriptingHost(
    val compiler: ScriptCompiler,
    val evaluator: ScriptEvaluator
) {
    /**
     * The overridable wrapper for executing evaluation in a desired coroutines context
     */
    open fun <T> runInCoroutineContext(block: suspend CoroutineScope.() -> T): T = runBlocking { block() }

    /**
     * The default implementation of the evaluation function
     */
    open fun eval(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration,
        configuration: ScriptEvaluationConfiguration?
    ): ResultWithDiagnostics<EvaluationResult> =
        runInCoroutineContext {
            compiler(script, scriptCompilationConfiguration).onSuccess {
                evaluator(it, configuration)
            }
        }
}
