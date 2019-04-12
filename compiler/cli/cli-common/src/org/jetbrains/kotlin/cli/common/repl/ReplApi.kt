/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.jvm.internal.TypeIntrinsics
import kotlin.reflect.KClass

const val REPL_CODE_LINE_FIRST_NO = 1
const val REPL_CODE_LINE_FIRST_GEN = 1

data class ReplCodeLine(val no: Int, val generation: Int, val code: String) : Serializable {
    companion object {
        private val serialVersionUID: Long = 8228357578L
    }
}

data class CompiledReplCodeLine(val className: String, val source: ReplCodeLine) : Serializable {
    companion object {
        private val serialVersionUID: Long = 8228307678L
    }
}

data class CompiledClassData(val path: String, val bytes: ByteArray) : Serializable {
    override fun equals(other: Any?): Boolean = (other as? CompiledClassData)?.let { path == it.path && Arrays.equals(bytes, it.bytes) } ?: false
    override fun hashCode(): Int = path.hashCode() + Arrays.hashCode(bytes)

    companion object {
        private val serialVersionUID: Long = 8228357578L
    }
}

interface CreateReplStageStateAction {
    fun createState(lock: ReentrantReadWriteLock = ReentrantReadWriteLock()): IReplStageState<*>
}

// --- check

interface ReplCheckAction {
    fun check(state: IReplStageState<*>, codeLine: ReplCodeLine): ReplCheckResult
}

sealed class ReplCheckResult : Serializable {
    class Ok : ReplCheckResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    class Incomplete : ReplCheckResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    class Error(val message: String, val location: CompilerMessageLocation? = null) : ReplCheckResult() {
        override fun toString(): String = "Error(message = \"$message\")"
        companion object { private val serialVersionUID: Long = 1L }
    }

    companion object {
        private val serialVersionUID: Long = 8228307678L
    }
}

// --- compile

interface ReplCompileAction {
    fun compile(state: IReplStageState<*>, codeLine: ReplCodeLine): ReplCompileResult
}

sealed class ReplCompileResult : Serializable {
    class CompiledClasses(val lineId: LineId,
                          val previousLines: List<ILineId>,
                          val mainClassName: String,
                          val classes: List<CompiledClassData>,
                          val hasResult: Boolean,
                          val classpathAddendum: List<File>,
                          val type: String?
    ) : ReplCompileResult() {
        companion object { private val serialVersionUID: Long = 2L }
    }

    class Incomplete : ReplCompileResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    class Error(val message: String, val location: CompilerMessageLocation? = null) : ReplCompileResult() {
        override fun toString(): String = "Error(message = \"$message\""
        companion object { private val serialVersionUID: Long = 1L }
    }

    companion object {
        private val serialVersionUID: Long = 8228307678L
    }
}

interface ReplCompiler : ReplCompileAction, ReplCheckAction, CreateReplStageStateAction

// --- eval

data class EvalClassWithInstanceAndLoader(val klass: KClass<*>, val instance: Any?, val classLoader: ClassLoader, val invokeWrapper: InvokeWrapper?)

interface ReplEvalAction {
    fun eval(state: IReplStageState<*>,
             compileResult: ReplCompileResult.CompiledClasses,
             scriptArgs: ScriptArgsWithTypes? = null,
             invokeWrapper: InvokeWrapper? = null): ReplEvalResult
}

sealed class ReplEvalResult : Serializable {
    class ValueResult(val name: String, val value: Any?, val type: String?) : ReplEvalResult() {
        override fun toString(): String {
            val v = if (value is Function<*>) "<function${TypeIntrinsics.getFunctionArity(value)}>" else value
            return "$name: $type = $v"
        }

        companion object { private val serialVersionUID: Long = 1L }
    }

    class UnitResult : ReplEvalResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    class Incomplete : ReplEvalResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    class HistoryMismatch(val lineNo: Int) : ReplEvalResult() {
        companion object { private val serialVersionUID: Long = 1L }
    }

    sealed class Error(val message: String) : ReplEvalResult() {
        class Runtime(message: String, val cause: Exception? = null) : Error(message) {
            companion object { private val serialVersionUID: Long = 1L }
        }

        class CompileTime(message: String, val location: CompilerMessageLocation? = null) : Error(message) {
            companion object { private val serialVersionUID: Long = 1L }
        }

        override fun toString(): String = "${this::class.simpleName}Error(message = \"$message\""

        companion object { private val serialVersionUID: Long = 1L }
    }

    companion object {
        private val serialVersionUID: Long = 8228307678L
    }
}

interface ReplEvaluator : ReplEvalAction, CreateReplStageStateAction

// --- compileAdnEval

interface ReplAtomicEvalAction {
    fun compileAndEval(state: IReplStageState<*>,
                       codeLine: ReplCodeLine,
                       scriptArgs: ScriptArgsWithTypes? = null,
                       invokeWrapper: InvokeWrapper? = null): ReplEvalResult
}

interface ReplAtomicEvaluator : ReplAtomicEvalAction, ReplCheckAction

interface ReplDelayedEvalAction {
    fun compileToEvaluable(state: IReplStageState<*>,
                           codeLine: ReplCodeLine,
                           defaultScriptArgs: ScriptArgsWithTypes? = null): Pair<ReplCompileResult, Evaluable?>
}

// other

interface Evaluable {
    val compiledCode: ReplCompileResult.CompiledClasses
    fun eval(scriptArgs: ScriptArgsWithTypes? = null, invokeWrapper: InvokeWrapper? = null): ReplEvalResult
}

interface ReplFullEvaluator : ReplEvaluator, ReplAtomicEvaluator, ReplDelayedEvalAction

/**
 * Keep args and arg types together, so as a whole they are present or absent
 */
class ScriptArgsWithTypes(val scriptArgs: Array<out Any?>, val scriptArgsTypes: Array<out KClass<out Any>>) : Serializable {
    init { assert(scriptArgs.size == scriptArgsTypes.size) }
    companion object {
        private val serialVersionUID: Long = 8529357500L
    }
}

enum class ReplRepeatingMode {
    NONE,
    REPEAT_ONLY_MOST_RECENT,
    REPEAT_ANY_PREVIOUS
}


interface InvokeWrapper {
    operator fun <T> invoke(body: () -> T): T // e.g. for capturing io
}
