/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.dependencies

import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

typealias Environment = Map<String, Any?>

interface ScriptDependenciesResolver {

    enum class ReportSeverity { FATAL, ERROR, WARNING, INFO, DEBUG }

    fun resolve(script: ScriptContents,
                environment: Environment?,
                report: (ReportSeverity, String, ScriptContents.Position?) -> Unit,
                previousDependencies: KotlinScriptExternalDependencies?
    ): Future<KotlinScriptExternalDependencies?> = PseudoFuture(null)
}

class BasicScriptDependenciesResolver : ScriptDependenciesResolver

fun KotlinScriptExternalDependencies?.asFuture(): PseudoFuture<KotlinScriptExternalDependencies?> = PseudoFuture(this)

class PseudoFuture<T>(private val value: T): Future<T> {
    override fun get(): T = value
    override fun get(p0: Long, p1: TimeUnit): T  = value
    override fun cancel(p0: Boolean): Boolean = false
    override fun isDone(): Boolean = true
    override fun isCancelled(): Boolean = false
}

interface ScriptContents {
    val file: File?
    val annotations: Iterable<Annotation>
    val text: CharSequence?

    data class Position(val line: Int, val col: Int)
}
