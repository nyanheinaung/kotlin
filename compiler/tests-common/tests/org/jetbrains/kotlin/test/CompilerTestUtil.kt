/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import org.jetbrains.kotlin.cli.common.CLITool
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertEquals

object CompilerTestUtil {
    @JvmStatic
    fun executeCompilerAssertSuccessful(compiler: CLITool<*>, args: List<String>) {
        val (output, exitCode) = executeCompiler(compiler, args)
        assertEquals(ExitCode.OK, exitCode, output)
    }

    @JvmStatic
    fun executeCompiler(compiler: CLITool<*>, args: List<String>): Pair<String, ExitCode> {
        val bytes = ByteArrayOutputStream()
        val origErr = System.err
        try {
            System.setErr(PrintStream(bytes))
            val exitCode = CLITool.doMainNoExit(compiler, args.toTypedArray())
            return Pair(String(bytes.toByteArray()), exitCode)
        }
        finally {
            System.setErr(origErr)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun compileJvmLibrary(
            src: File,
            libraryName: String = "library",
            extraOptions: List<String> = emptyList(),
            extraClasspath: List<File> = emptyList()
    ): File {
        val destination = File(KotlinTestUtils.tmpDir("testLibrary"), "$libraryName.jar")
        val args = mutableListOf<String>().apply {
            add(src.path)
            add("-d")
            add(destination.path)
            if (extraClasspath.isNotEmpty()) {
                add("-cp")
                add(extraClasspath.joinToString(":") { it.path })
            }
            addAll(extraOptions)
        }
        executeCompilerAssertSuccessful(K2JVMCompiler(), args)
        return destination
    }
}
