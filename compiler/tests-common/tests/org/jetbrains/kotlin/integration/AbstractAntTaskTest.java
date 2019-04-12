/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.integration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime;

import java.io.File;

public abstract class AbstractAntTaskTest extends KotlinIntegrationTestBase {
    protected void doTest(String testFile) throws Exception {
        String testDataDir = new File(testFile).getAbsolutePath();

        String antClasspath = System.getProperty("kotlin.ant.classpath");
        if (antClasspath == null) {
            throw new RuntimeException("Unable to get a valid classpath from 'kotlin.ant.classpath' property, please set it accordingly");
        }

        String antLauncherClass = System.getProperty("kotlin.ant.launcher.class");
        if (antLauncherClass == null) {
            throw new RuntimeException("Unable to get a valid class FQN from 'kotlin.ant.launcher.class' property, please set it accordingly");
        }

        runJava(
                testDataDir,
                "build.log",
                "-Xmx192m",
                "-Dkotlin.lib=" + KotlinIntegrationTestBase.getCompilerLib(),
                "-Dkotlin.runtime.jar=" + ForTestCompileRuntime.runtimeJarForTests().getAbsolutePath(),
                "-Dkotlin.reflect.jar=" + ForTestCompileRuntime.reflectJarForTests().getAbsolutePath(),
                "-Dtest.data=" + testDataDir,
                "-Dtemp=" + tmpdir,
                "-cp", antClasspath,
                antLauncherClass,
                "-f", "build.xml"
        );
    }

    @Override
    @NotNull
    protected String normalizeOutput(@NotNull File testDataDir, @NotNull String content) {
        return super.normalizeOutput(testDataDir, content)
                .replaceAll("Total time: .+\n", "Total time: [time]\n");
    }
}
