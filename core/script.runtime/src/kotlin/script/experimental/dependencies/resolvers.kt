/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.experimental.dependencies

import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.dependencies.DependenciesResolver.ResolveResult

interface DependenciesResolver : ScriptDependenciesResolver {
    fun resolve(scriptContents: ScriptContents, environment: Environment): ResolveResult

    object NoDependencies : DependenciesResolver {
        override fun resolve(scriptContents: ScriptContents, environment: Environment) = ScriptDependencies.Empty.asSuccess()
    }

    sealed class ResolveResult {
        abstract val dependencies: ScriptDependencies?
        abstract val reports: List<ScriptReport>

        data class Success(
                override val dependencies: ScriptDependencies,
                override val reports: List<ScriptReport> = listOf()
        ) : ResolveResult()

        data class Failure(override val reports: List<ScriptReport>) : ResolveResult() {
            constructor(vararg reports: ScriptReport) : this(reports.asList())

            override val dependencies: ScriptDependencies? get() = null
        }
    }
}

data class ScriptReport(val message: String, val severity: Severity = Severity.ERROR, val position: Position? = null) {
    data class Position(val startLine: Int, val startColumn: Int, val endLine: Int? = null, val endColumn: Int? = null)
    enum class Severity { FATAL, ERROR, WARNING, INFO, DEBUG }
}

fun ScriptDependencies.asSuccess(): ResolveResult.Success = ResolveResult.Success(this)