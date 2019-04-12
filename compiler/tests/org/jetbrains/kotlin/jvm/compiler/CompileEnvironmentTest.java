/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler;

import com.intellij.openapi.util.io.FileUtil;
import junit.framework.TestCase;
import org.jetbrains.kotlin.cli.common.ExitCode;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CompileEnvironmentTest extends TestCase {

    public void testSmokeWithCompilerOutput() throws IOException {
        File tempDir = FileUtil.createTempDirectory("compilerTest", "compilerTest");
        try {
            File out = new File(tempDir, "out");
            File stdlib = ForTestCompileRuntime.runtimeJarForTests();
            ExitCode exitCode = new K2JVMCompiler().exec(
                    System.out,
                    KotlinTestUtils.getTestDataPathBase() + "/compiler/smoke/Smoke.kt",
                    "-d", out.getAbsolutePath(),
                    "-no-stdlib",
                    "-classpath", stdlib.getAbsolutePath()
            );
            Assert.assertEquals(ExitCode.OK, exitCode);
            File[] files = out.listFiles();
            Arrays.sort(files);
            assertEquals(2, files.length);
            assertEquals(1, files[0].listFiles().length); //META-INF
            assertEquals(1, files[1].listFiles().length); // SmokeKt
        }
        finally {
            FileUtil.delete(tempDir);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        KtUsefulTestCase.resetApplicationToNull();
    }
}
