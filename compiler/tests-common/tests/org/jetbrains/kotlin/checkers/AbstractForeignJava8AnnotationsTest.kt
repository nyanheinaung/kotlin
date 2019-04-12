/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers

import org.jetbrains.kotlin.test.InTextDirectivesUtils
import java.io.File

abstract class AbstractForeignJava8AnnotationsTest : AbstractForeignAnnotationsTest() {
    override val annotationsPath: String
        get() = JAVA8_ANNOTATION_SOURCES_PATH
}

abstract class AbstractForeignJava8AnnotationsNoAnnotationInClasspathTest : AbstractForeignAnnotationsNoAnnotationInClasspathTest() {
    override val annotationsPath: String
        get() = JAVA8_ANNOTATION_SOURCES_PATH

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        if (skipForCompiledVersion(files)) return
        super.analyzeAndCheck(testDataFile, files)
    }
}

abstract class AbstractForeignJava8AnnotationsNoAnnotationInClasspathWithFastClassReadingTest : AbstractForeignAnnotationsNoAnnotationInClasspathWithFastClassReadingTest() {
    override val annotationsPath: String
        get() = JAVA8_ANNOTATION_SOURCES_PATH

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        if (skipForCompiledVersion(files)) return
        super.analyzeAndCheck(testDataFile, files)
    }
}

private fun skipForCompiledVersion(files: List<BaseDiagnosticsTest.TestFile>) =
        files.any { file -> InTextDirectivesUtils.isDirectiveDefined(file.expectedText, "// SKIP_COMPILED_JAVA") }


private const val JAVA8_ANNOTATION_SOURCES_PATH = "third-party/jdk8-annotations"
