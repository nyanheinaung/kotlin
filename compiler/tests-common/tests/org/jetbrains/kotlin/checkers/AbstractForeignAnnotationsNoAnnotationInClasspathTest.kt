/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers

import org.jetbrains.kotlin.codegen.CodegenTestUtil
import org.jetbrains.kotlin.test.InTextDirectivesUtils
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractForeignAnnotationsNoAnnotationInClasspathTest : AbstractForeignAnnotationsTest() {
    // This should be executed after setUp runs, since setUp changes the root folder
    // for temporary files.
    private val compiledJavaPath by lazy {
        KotlinTestUtils.tmpDir("java-compiled-files")
    }

    override fun getExtraClasspath(): List<File> {
        val foreignAnnotations = createJarWithForeignAnnotations()
        val testAnnotations = compileTestAnnotations(foreignAnnotations)

        val additionalClasspath = (foreignAnnotations + testAnnotations).map { it.path }
        CodegenTestUtil.compileJava(
                CodegenTestUtil.findJavaSourcesInDirectory(javaFilesDir),
                additionalClasspath, emptyList(),
                compiledJavaPath
        )

        return listOf(compiledJavaPath) + testAnnotations
    }

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        if (files.any { file -> InTextDirectivesUtils.isDirectiveDefined(file.expectedText, "// SOURCE_RETENTION_ANNOTATIONS") }) return
        super.analyzeAndCheck(testDataFile, files)
    }

    override fun isJavaSourceRootNeeded() = false
    override fun skipDescriptorsValidation() = true
}
