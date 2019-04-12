/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test;

import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime;
import org.jetbrains.kotlin.utils.PathUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class KotlinWithJdkAndRuntimeLightProjectDescriptor extends KotlinJdkAndLibraryProjectDescriptor {
    protected KotlinWithJdkAndRuntimeLightProjectDescriptor() {
        super(ForTestCompileRuntime.runtimeJarForTests());
    }

    public KotlinWithJdkAndRuntimeLightProjectDescriptor(@NotNull List<? extends File> libraryFiles) {
        super(libraryFiles);
    }

    @NotNull
    public static final KotlinWithJdkAndRuntimeLightProjectDescriptor INSTANCE = new KotlinWithJdkAndRuntimeLightProjectDescriptor();

    @NotNull
    public static final KotlinWithJdkAndRuntimeLightProjectDescriptor INSTANCE_WITH_KOTLIN_TEST = new KotlinWithJdkAndRuntimeLightProjectDescriptor(
            Arrays.asList(ForTestCompileRuntime.runtimeJarForTests(),
                          PathUtil.getKotlinPathsForDistDirectory().getKotlinTestPath())
    );

    @NotNull
    public static final KotlinWithJdkAndRuntimeLightProjectDescriptor INSTANCE_WITH_SCRIPT_RUNTIME = new KotlinWithJdkAndRuntimeLightProjectDescriptor(
            Arrays.asList(ForTestCompileRuntime.runtimeJarForTests(),
                          PathUtil.getKotlinPathsForDistDirectory().getScriptRuntimePath())
    );

    @NotNull
    public static final KotlinWithJdkAndRuntimeLightProjectDescriptor INSTANCE_WITH_REFLECT = new KotlinWithJdkAndRuntimeLightProjectDescriptor(
            Arrays.asList(ForTestCompileRuntime.runtimeJarForTests(),
                          ForTestCompileRuntime.reflectJarForTests())
    );

    @NotNull
    public static final KotlinWithJdkAndRuntimeLightProjectDescriptor INSTANCE_FULL_JDK = new KotlinWithJdkAndRuntimeLightProjectDescriptor() {
        @Override
        public Sdk getSdk() {
            return PluginTestCaseBase.fullJdk();
        }
    };
}
