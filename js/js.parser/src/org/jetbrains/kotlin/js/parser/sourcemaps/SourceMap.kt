/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.parser.sourcemaps

import java.io.File
import java.io.PrintStream
import java.io.Reader

class SourceMap(val sourceContentResolver: (String) -> Reader?) {
    val groups = mutableListOf<SourceMapGroup>()

    fun debug(writer: PrintStream = System.out) {
        for ((index, group) in groups.withIndex()) {
            writer.print("${index + 1}:")
            for (segment in group.segments) {
                writer.print(" ${segment.generatedColumnNumber + 1}:${segment.sourceLineNumber + 1},${segment.sourceColumnNumber + 1}")
            }
            writer.println()
        }
    }

    fun debugVerbose(writer: PrintStream, generatedJsFile: File) {
        assert(generatedJsFile.exists()) { "$generatedJsFile does not exist!" }
        val generatedLines = generatedJsFile.readLines().toTypedArray()
        for ((index, group) in groups.withIndex()) {
            writer.print("${index + 1}:")
            val generatedLine = generatedLines[index]
            val segmentsByColumn = group.segments.map { it.generatedColumnNumber to it }.toMap()
            for (i in generatedLine.indices) {
                segmentsByColumn[i]?.let { (_, sourceFile, sourceLine, sourceColumn) ->
                    writer.print("<$sourceFile:${sourceLine + 1}:${sourceColumn + 1}>")
                }
                writer.print(generatedLine[i])
            }
            writer.println()
        }
    }
}

data class SourceMapSegment(
    val generatedColumnNumber: Int,
    val sourceFileName: String?,
    val sourceLineNumber: Int,
    val sourceColumnNumber: Int
)

class SourceMapGroup {
    val segments = mutableListOf<SourceMapSegment>()
}

sealed class SourceMapParseResult

class SourceMapSuccess(val value: SourceMap) : SourceMapParseResult()

class SourceMapError(val message: String) : SourceMapParseResult()