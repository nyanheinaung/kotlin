/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.PartialAnalysisHandlerExtension
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractKapt3BuilderModeBytecodeShapeTest : CodegenTestCase() {
    private companion object {
        var TEST_LIGHT_ANALYSIS: ClassBuilderFactory = object : ClassBuilderFactories.TestClassBuilderFactory() {
            override fun getClassBuilderMode() = ClassBuilderMode.KAPT3
        }
    }

    override fun doMultiFileTest(wholeFile: File, files: MutableList<TestFile>, javaFilesDir: File?) {
        val txtFile = File(wholeFile.parentFile, wholeFile.nameWithoutExtension + ".txt")
        compile(files, javaFilesDir)
        KotlinTestUtils.assertEqualsToFile(txtFile, BytecodeListingTextCollectingVisitor.getText(classFileFactory))
    }

    override fun setupEnvironment(environment: KotlinCoreEnvironment) {
        AnalysisHandlerExtension.registerExtension(environment.project, PartialAnalysisHandlerExtension())
    }

    override fun getClassBuilderFactory(): ClassBuilderFactory {
        return TEST_LIGHT_ANALYSIS
    }

    override fun verifyWithDex(): Boolean {
        return false
    }
}