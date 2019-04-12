/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import java.io.File

abstract class AbstractBlackBoxInlineCodegenTest : AbstractBlackBoxCodegenTest() {
    override fun doMultiFileTest(wholeFile: File, files: List<TestFile>, javaFilesDir: File?) {
        super.doMultiFileTest(wholeFile, files, javaFilesDir)
        try {
            InlineTestUtil.checkNoCallsToInline(initializedClassLoader.allGeneratedFiles.filterClassFiles(), myFiles.psiFiles)
            SMAPTestUtil.checkSMAP(files, generateClassesInFile().getClassFiles(), false)
        }
        catch (e: Throwable) {
            println(generateToText())
            throw e
        }
    }
}
