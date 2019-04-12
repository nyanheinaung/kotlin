/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File

abstract class AbstractBlackBoxAgainstJavaCodegenTest : AbstractBlackBoxCodegenTest() {
    override fun doMultiFileTest(wholeFile: File, files: MutableList<TestFile>, javaFilesDir: File?) {
        javaClassesOutputDirectory = javaFilesDir!!.let { directory ->
            CodegenTestUtil.compileJava(CodegenTestUtil.findJavaSourcesInDirectory(directory), emptyList(), extractJavacOptions(files))
        }

        super.doMultiFileTest(wholeFile, files, null)
    }

    override fun updateConfiguration(configuration: CompilerConfiguration) {
        configuration.addJvmClasspathRoot(javaClassesOutputDirectory)

        if (configuration.get(JVMConfigurationKeys.USE_FAST_CLASS_FILES_READING) == null) {
            // By default (unless disabled in the test with a directive), use the fast class reading mode
            configuration.put(JVMConfigurationKeys.USE_FAST_CLASS_FILES_READING, true)
        }
    }
}
