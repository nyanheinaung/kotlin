/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.script

import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

const val DEFAULT_SCRIPT_FILE_PATTERN = ".*\\.kts"

// TODO: remove this file and all the usages after releasing GSK 1.0

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.templatesScriptTemplateDefinition instead",
            replaceWith = ReplaceWith("kotlin.script.templates.ScriptTemplateDefinition"))
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScriptTemplateDefinition(val resolver: KClass<out ScriptDependenciesResolver>,
                                          val scriptFilePattern: String = DEFAULT_SCRIPT_FILE_PATTERN)

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.extensions.SamWithReceiverAnnotations instead",
            replaceWith = ReplaceWith("kotlin.script.extensions.SamWithReceiverAnnotations"))
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SamWithReceiverAnnotations(vararg val annotations: String)

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.dependencies.ScriptContents instead",
            replaceWith = ReplaceWith("kotlin.script.dependencies.ScriptContents"))
interface ScriptContents {

    data class Position(val line: Int, val col: Int)

    val file: File?
    val annotations: Iterable<Annotation>
    val text: CharSequence?
}

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.dependencies.PseudoFuture instead",
            replaceWith = ReplaceWith("kotlin.script.dependencies.PseudoFuture"))
class PseudoFuture<T>(private val value: T): Future<T> {
    override fun get(): T = value
    override fun get(p0: Long, p1: TimeUnit): T  = value
    override fun cancel(p0: Boolean): Boolean = false
    override fun isDone(): Boolean = true
    override fun isCancelled(): Boolean = false
}

fun KotlinScriptExternalDependencies?.asFuture(): PseudoFuture<KotlinScriptExternalDependencies?> = PseudoFuture(this)

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.dependencies.ScriptDependenciesResolver instead",
            replaceWith = ReplaceWith("kotlin.script.dependencies.ScriptDependenciesResolver"))
interface ScriptDependenciesResolver {

    enum class ReportSeverity { FATAL, ERROR, WARNING, INFO, DEBUG }

    fun resolve(script: ScriptContents,
                environment: Map<String, Any?>?,
                report: (ReportSeverity, String, ScriptContents.Position?) -> Unit,
                previousDependencies: KotlinScriptExternalDependencies?
    ): Future<KotlinScriptExternalDependencies?> = PseudoFuture(null)
}

@Suppress("unused")
@Deprecated("Used only for compatibility with legacy code, use kotlin.script.dependencies.ScriptDependenciesResolver instead",
            replaceWith = ReplaceWith("kotlin.script.dependencies.ScriptDependenciesResolver"))
interface ScriptDependenciesResolverEx {
    fun resolve(script: ScriptContents,
                environment: Map<String, Any?>?,
                previousDependencies: KotlinScriptExternalDependencies?
    ): KotlinScriptExternalDependencies? = null
}

@Deprecated("Used only for compatibility with legacy code, use kotlin.script.dependencies.KotlinScriptExternalDependencies instead",
            replaceWith = ReplaceWith("kotlin.script.dependencies.KotlinScriptExternalDependencies"))
interface KotlinScriptExternalDependencies : Comparable<KotlinScriptExternalDependencies> {
    val javaHome: String? get() = null
    val classpath: Iterable<File> get() = emptyList()
    val imports: Iterable<String> get() = emptyList()
    val sources: Iterable<File> get() = emptyList()
    val scripts: Iterable<File> get() = emptyList()

    override fun compareTo(other: KotlinScriptExternalDependencies): Int =
            compareValues(javaHome, other.javaHome)
                    .chainCompare { compareIterables(classpath, other.classpath) }
                    .chainCompare { compareIterables(imports, other.imports) }
                    .chainCompare { compareIterables(sources, other.sources) }
                    .chainCompare { compareIterables(scripts, other.scripts) }
}

private fun<T: Comparable<T>> compareIterables(a: Iterable<T>, b: Iterable<T>): Int {
    val ia = a.iterator()
    val ib = b.iterator()
    while (true) {
        if (ia.hasNext() && !ib.hasNext()) return 1
        if (!ia.hasNext() && !ib.hasNext()) return 0
        if (!ia.hasNext()) return -1
        val compRes = compareValues(ia.next(), ib.next())
        if (compRes != 0) return compRes
    }
}

private inline fun Int.chainCompare(compFn: () -> Int ): Int = if (this != 0) this else compFn()

