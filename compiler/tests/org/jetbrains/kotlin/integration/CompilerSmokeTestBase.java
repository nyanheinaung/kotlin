/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.integration;

import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.utils.StringsKt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class CompilerSmokeTestBase extends KotlinIntegrationTestBase {
    @NotNull
    protected String getTestDataDir() {
        return KotlinTestUtils.getTestDataPathBase() + "/integration/smoke/" + getTestName(true);
    }

    protected int run(String logName, String... args) throws Exception {
        return runJava(getTestDataDir(), logName, args);
    }

    protected int runCompiler(String logName, String... arguments) throws Exception {
        Collection<String> javaArgs = new ArrayList<>();

        javaArgs.add("-cp");
        javaArgs.add(StringsKt.join(Arrays.asList(
                getCompilerLib().getAbsolutePath() + File.separator + "kotlin-compiler.jar"
        ), File.pathSeparator));
        javaArgs.add("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler");

        Collections.addAll(javaArgs, arguments);

        return run(logName, ArrayUtil.toStringArray(javaArgs));
    }
}
