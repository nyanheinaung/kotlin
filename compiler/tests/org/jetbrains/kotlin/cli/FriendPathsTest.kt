/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli

import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.test.CompilerTestUtil
import org.jetbrains.kotlin.test.TestCaseWithTmpdir
import java.io.File

class FriendPathsTest : TestCaseWithTmpdir() {
    private fun getTestDataDirectory(): File = File("compiler/testData/friendPaths/")

    fun testArchive() {
        val libSrc = File(getTestDataDirectory(), "lib.kt")
        val libDest = File(tmpdir, "lib.jar")
        CompilerTestUtil.executeCompilerAssertSuccessful(K2JVMCompiler(), listOf("-d", libDest.path, libSrc.path))

        CompilerTestUtil.executeCompilerAssertSuccessful(
            K2JVMCompiler(),
            listOf(
                "-d", tmpdir.path, "-cp", libDest.path, File(getTestDataDirectory(), "usage.kt").path,
                "-Xfriend-paths=${libDest.path}"
            )
        )
    }

    fun testDirectory() {
        val libSrc = File(getTestDataDirectory(), "lib.kt")
        val libDest = File(tmpdir, "lib")
        CompilerTestUtil.executeCompilerAssertSuccessful(K2JVMCompiler(), listOf("-d", libDest.path, libSrc.path))

        CompilerTestUtil.executeCompilerAssertSuccessful(
            K2JVMCompiler(),
            listOf(
                "-d", tmpdir.path, "-cp", libDest.path, File(getTestDataDirectory(), "usage.kt").path,
                "-Xfriend-paths=${libDest.path}"
            )
        )
    }
}
