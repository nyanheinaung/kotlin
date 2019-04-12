/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import java.io.File

abstract class AbstractCompileKotlinAgainstInlineKotlinTest : AbstractCompileKotlinAgainstKotlinTest() {
    override fun doMultiFileTest(wholeFile: File, files: List<TestFile>, javaFilesDir: File?) {
        val (factory1, factory2) = doTwoFileTest(files.filter { it.name.endsWith(".kt") })
        try {
            val allGeneratedFiles = factory1.asList() + factory2.asList()
            val sourceFiles = factory1.inputFiles + factory2.inputFiles
            InlineTestUtil.checkNoCallsToInline(allGeneratedFiles.filterClassFiles(), sourceFiles)
            SMAPTestUtil.checkSMAP(files, allGeneratedFiles.filterClassFiles(), true)
        }
        catch (e: Throwable) {
            println("FIRST:\n\n${factory1.createText()}\n\nSECOND:\n\n${factory2.createText()}")
            throw e
        }
    }
}
