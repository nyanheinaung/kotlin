/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

object SMAPParser {
    /*null smap means that there is no any debug info in file (e.g. sourceName)*/
    @JvmStatic
    fun parseOrCreateDefault(mappingInfo: String?, source: String?, path: String, methodStartLine: Int, methodEndLine: Int): SMAP {
        if (mappingInfo != null && mappingInfo.isNotEmpty()) {
            return parse(mappingInfo)
        }

        val mapping =
            if (source == null || source.isEmpty() || methodStartLine > methodEndLine)
                FileMapping.SKIP
            else
                FileMapping(source, path).apply {
                    if (methodStartLine <= methodEndLine) {
                        //one to one
                        addRangeMapping(RangeMapping(methodStartLine, methodStartLine, methodEndLine - methodStartLine + 1))
                    }
                }

        return SMAP(listOf(mapping))
    }

    @JvmStatic
    fun parse(mappingInfo: String): SMAP {
        val fileMappings = linkedMapOf<Int, FileMapping>()

        val iterator = mappingInfo.lineSequence().dropWhile { it.trim() != SMAP.FILE_SECTION }.drop(1).iterator()
        while (iterator.hasNext()) {
            val fileDeclaration = iterator.next().trim()
            if (fileDeclaration == SMAP.LINE_SECTION) break

            if (!fileDeclaration.startsWith('+')) {
                throw AssertionError("File declaration should be in extended form, but: $fileDeclaration in $mappingInfo")
            }

            val indexAndFileInternalName = fileDeclaration.substringAfter("+ ").trim()
            val fileIndex = indexAndFileInternalName.substringBefore(' ').toInt()
            val fileName = indexAndFileInternalName.substringAfter(' ')
            val path = iterator.next().trim()
            fileMappings[fileIndex] = FileMapping(fileName, path)
        }

        for (lineMapping in iterator) {
            if (lineMapping.trim() == SMAP.END) break
            /*only simple mapping now*/
            val targetSplit = lineMapping.indexOf(':')
            val originalPart = lineMapping.substring(0, targetSplit)
            val rangeSeparator = originalPart.indexOf(',').let { if (it < 0) targetSplit else it }

            val fileSeparator = lineMapping.indexOf('#')
            val originalIndex = originalPart.substring(0, fileSeparator).toInt()
            val range = if (rangeSeparator == targetSplit) 1 else originalPart.substring(rangeSeparator + 1, targetSplit).toInt()

            val fileIndex = lineMapping.substring(fileSeparator + 1, rangeSeparator).toInt()
            val targetIndex = lineMapping.substring(targetSplit + 1).toInt()
            fileMappings[fileIndex]!!.addRangeMapping(RangeMapping(originalIndex, targetIndex, range))
        }

        return SMAP(fileMappings.values.toList())
    }
}
