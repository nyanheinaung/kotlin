/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.incremental.utils.TestCompilationResult
import org.jetbrains.kotlin.incremental.utils.TestICReporter
import org.jetbrains.kotlin.incremental.utils.TestMessageCollector
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.ByteArrayOutputStream
import java.io.File
import javax.tools.ToolProvider

abstract class AbstractIncrementalJvmCompilerRunnerTest : AbstractIncrementalCompilerRunnerTestBase<K2JVMCompilerArguments>() {
    override fun make(cacheDir: File, sourceRoots: Iterable<File>, args: K2JVMCompilerArguments): TestCompilationResult {
        val reporter = TestICReporter()
        val messageCollector = TestMessageCollector()
        makeIncrementally(cacheDir, sourceRoots, args, reporter = reporter, messageCollector = messageCollector)
        val kotlinCompileResult = TestCompilationResult(reporter, messageCollector)
        if (kotlinCompileResult.exitCode != ExitCode.OK) return kotlinCompileResult

        val (javaExitCode, _, javaErrors) = compileJava(sourceRoots, args.destination!!)
        return when (javaExitCode) {
            ExitCode.OK -> kotlinCompileResult
            else -> kotlinCompileResult.copy(exitCode = javaExitCode, compileErrors = javaErrors)
        }
    }

    private fun compileJava(sourceRoots: Iterable<File>, kotlinClassesPath: String): TestCompilationResult {
        val javaSources = arrayListOf<File>()
        for (root in sourceRoots) {
            javaSources.addAll(root.walk().filter { it.isFile && it.extension == "java" })
        }
        if (javaSources.isEmpty()) return TestCompilationResult(ExitCode.OK, emptyList(), emptyList())

        val javaClasspath = compileClasspath + File.pathSeparator + kotlinClassesPath
        val javaDestinationDir = File(workingDir, "java-classes").apply {
            if (exists()) {
                deleteRecursively()
            }
            mkdirs()
        }
        val args = arrayOf("-cp", javaClasspath,
                           "-d", javaDestinationDir.canonicalPath,
                           *javaSources.map { it.canonicalPath }.toTypedArray()
        )

        val err = ByteArrayOutputStream()
        val javac = ToolProvider.getSystemJavaCompiler()
        val rc = javac.run(null, null, err, *args)

        val exitCode = if (rc == 0) ExitCode.OK else ExitCode.COMPILATION_ERROR
        val errors = err.toString().split("\n")
        return TestCompilationResult(exitCode, javaSources, errors)
    }

    override fun createCompilerArguments(destinationDir: File, testDir: File): K2JVMCompilerArguments =
        K2JVMCompilerArguments().apply {
            moduleName = testDir.name
            destination = destinationDir.path
            classpath = compileClasspath
        }

    private val compileClasspath =
        listOf(
            kotlinStdlibJvm,
            KotlinTestUtils.getAnnotationsJar()
        ).joinToString(File.pathSeparator) { it.canonicalPath }
}
