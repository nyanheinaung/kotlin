/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream

class TestStdlibWithDxTest {
    @Test fun testRuntimeWithDx() {
        doTest(ForTestCompileRuntime.runtimeJarForTests())
    }

    @Test fun testReflectWithDx() {
        doTest(ForTestCompileRuntime.reflectJarForTests())
    }

    private fun doTest(file: File) {
        ZipInputStream(FileInputStream(file)).use { zip ->
            for (entry in generateSequence { zip.nextEntry }) {
                if (entry.name.endsWith(".class") && !entry.name.startsWith("META-INF/")) {
                    DxChecker.checkFileWithDx(zip.readBytes(), entry.name)
                }
            }
        }
    }
}
