/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import com.google.common.base.Throwables
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.LineSeparator
import org.jetbrains.kotlin.utils.repl.ReplEscapeType
import java.io.File
import java.net.URLClassLoader

// using '#' to avoid collisions with xml escaping
private val SOURCE_CHARS: List<String> = listOf("\r", "\n", "#")
private val XML_REPLACEMENTS: List<String> = listOf("#r", "#n", "#diez")

private val END_LINE: String = LineSeparator.getSystemLineSeparator().separatorString
private const val XML_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

fun String.replUnescapeLineBreaks() = StringUtil.replace(this, XML_REPLACEMENTS, SOURCE_CHARS)
fun String.replEscapeLineBreaks() = StringUtil.replace(this, SOURCE_CHARS, XML_REPLACEMENTS)

fun String.replOutputAsXml(escapeType: ReplEscapeType): String {
    val escapedXml = StringUtil.escapeXml(replEscapeLineBreaks())
    return "$XML_PREAMBLE<output type=\"$escapeType\">$escapedXml</output>"
}

fun String.replInputAsXml(): String {
    val escapedXml = StringUtil.escapeXml(replEscapeLineBreaks())
    return "$XML_PREAMBLE<input>$escapedXml</input>"
}

fun String.replAddLineBreak() = this + END_LINE
fun String.replRemoveLineBreaksInTheEnd() = trimEnd(*END_LINE.toCharArray())
fun String.replNormalizeLineBreaks() = replace(END_LINE, "\n")

fun makeScriptBaseName(codeLine: ReplCodeLine) =
        "Line_${codeLine.no}${if (codeLine.generation > REPL_CODE_LINE_FIRST_GEN) "_gen_${codeLine.generation}" else ""}"

fun scriptResultFieldName(lineNo: Int) = "res$lineNo"

fun renderReplStackTrace(cause: Throwable, startFromMethodName: String): String {
    val newTrace = arrayListOf<StackTraceElement>()
    var skip = true
    for (element in cause.stackTrace.reversed()) {
        if ("${element.className}.${element.methodName}" == startFromMethodName) {
            skip = false
        }
        if (!skip) {
            newTrace.add(element)
        }
    }

    val resultingTrace = newTrace.reversed().dropLast(1)

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UsePropertyAccessSyntax")
    (cause as java.lang.Throwable).setStackTrace(resultingTrace.toTypedArray())

    return Throwables.getStackTraceAsString(cause).trimEnd()
}

internal fun ClassLoader.listAllUrlsAsFiles(): List<File> {
    val parents = generateSequence(this) { loader -> loader.parent }.filterIsInstance(URLClassLoader::class.java)
    return parents.fold(emptyList<File>()) { accum, loader ->
        loader.listLocalUrlsAsFiles() + accum
    }.distinct()
}

internal fun URLClassLoader.listLocalUrlsAsFiles(): List<File> {
    return this.urLs.mapNotNull { it.toString().removePrefix("file:") }.map(::File)
}

internal fun <T : Any> List<T>.ensureNotEmpty(error: String): List<T> {
    if (this.isEmpty()) throw IllegalStateException(error)
    return this
}