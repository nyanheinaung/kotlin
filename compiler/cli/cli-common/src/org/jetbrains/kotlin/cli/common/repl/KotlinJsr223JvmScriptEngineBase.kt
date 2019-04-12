/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import java.io.Reader
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.script.*

const val KOTLIN_SCRIPT_STATE_BINDINGS_KEY = "kotlin.script.state"
const val KOTLIN_SCRIPT_ENGINE_BINDINGS_KEY = "kotlin.script.engine"

abstract class KotlinJsr223JvmScriptEngineBase(protected val myFactory: ScriptEngineFactory) : AbstractScriptEngine(), ScriptEngine, Compilable {

    protected abstract val replCompiler: ReplCompiler
    protected abstract val replEvaluator: ReplFullEvaluator

    override fun eval(script: String, context: ScriptContext): Any? = compileAndEval(script, context)

    override fun eval(script: Reader, context: ScriptContext): Any? = compileAndEval(script.readText(), context)

    override fun compile(script: String): CompiledScript = compile(script, getContext())

    override fun compile(script: Reader): CompiledScript = compile(script.readText(), getContext())

    override fun createBindings(): Bindings = SimpleBindings().apply { put(KOTLIN_SCRIPT_ENGINE_BINDINGS_KEY, this) }

    override fun getFactory(): ScriptEngineFactory = myFactory

    // the parameter could be used in the future when we decide to keep state completely in the context and solve appropriate problems (now e.g. replCompiler keeps separate state)
    fun nextCodeLine(context: ScriptContext, code: String) = getCurrentState(context).let { ReplCodeLine(it.getNextLineNo(), it.currentGeneration, code) }

    protected abstract fun createState(lock: ReentrantReadWriteLock = ReentrantReadWriteLock()): IReplStageState<*>

    protected fun getCurrentState(context: ScriptContext) =
            context.getBindings(ScriptContext.ENGINE_SCOPE)
                    .getOrPut(KOTLIN_SCRIPT_STATE_BINDINGS_KEY, {
                        // TODO: check why createBinding is not called on creating default context, so the engine is not set
                        context.getBindings(ScriptContext.ENGINE_SCOPE).put(KOTLIN_SCRIPT_ENGINE_BINDINGS_KEY, this@KotlinJsr223JvmScriptEngineBase)
                        createState()
                    }) as IReplStageState<*>

    open fun overrideScriptArgs(context: ScriptContext): ScriptArgsWithTypes? = null

    open fun compileAndEval(script: String, context: ScriptContext): Any? {
        val codeLine = nextCodeLine(context, script)
        val state = getCurrentState(context)
        val result = replEvaluator.compileAndEval(state, codeLine, scriptArgs = overrideScriptArgs(context))
        return when (result) {
            is ReplEvalResult.ValueResult -> result.value
            is ReplEvalResult.UnitResult -> null
            is ReplEvalResult.Error -> throw ScriptException(result.message)
            is ReplEvalResult.Incomplete -> throw ScriptException("error: incomplete code")
            is ReplEvalResult.HistoryMismatch -> throw ScriptException("Repl history mismatch at line: ${result.lineNo}")
        }
    }

    open fun compile(script: String, context: ScriptContext): CompiledScript {
        val codeLine = nextCodeLine(context, script)
        val state = getCurrentState(context)

        val result = replCompiler.compile(state, codeLine)
        val compiled = when (result) {
            is ReplCompileResult.Error -> throw ScriptException("Error${result.locationString()}: ${result.message}")
            is ReplCompileResult.Incomplete -> throw ScriptException("error: incomplete code")
            is ReplCompileResult.CompiledClasses -> result
        }
        return CompiledKotlinScript(this, codeLine, compiled)
    }

    open fun eval(compiledScript: CompiledKotlinScript, context: ScriptContext): Any? {
        val state = getCurrentState(context)
        val result = try {
            replEvaluator.eval(state, compiledScript.compiledData, scriptArgs = overrideScriptArgs(context))
        }
        catch (e: Exception) {
            throw ScriptException(e)
        }

        return when (result) {
            is ReplEvalResult.ValueResult -> result.value
            is ReplEvalResult.UnitResult -> null
            is ReplEvalResult.Error -> throw ScriptException(result.message)
            is ReplEvalResult.Incomplete -> throw ScriptException("error: incomplete code")
            is ReplEvalResult.HistoryMismatch -> throw ScriptException("Repl history mismatch at line: ${result.lineNo}")
        }
    }

    class CompiledKotlinScript(val engine: KotlinJsr223JvmScriptEngineBase, val codeLine: ReplCodeLine, val compiledData: ReplCompileResult.CompiledClasses) : CompiledScript() {
        override fun eval(context: ScriptContext): Any? = engine.eval(this, context)
        override fun getEngine(): ScriptEngine = engine
    }
}

private fun ReplCompileResult.Error.locationString() =
        if (location == null) ""
        else " at ${location.line}:${location.column}"
