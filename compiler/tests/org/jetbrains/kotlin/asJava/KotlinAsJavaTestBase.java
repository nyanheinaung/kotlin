/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.finder.JavaElementFinder;
import org.jetbrains.kotlin.cli.common.config.ContentRootsKt;
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.test.ConfigurationKind;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.KotlinTestWithEnvironment;
import org.jetbrains.kotlin.test.TestJdkKind;

import java.io.File;
import java.util.List;

public abstract class KotlinAsJavaTestBase extends KotlinTestWithEnvironment {
    protected JavaElementFinder finder;

    @Override
    protected KotlinCoreEnvironment createEnvironment() {
        CompilerConfiguration configuration = KotlinTestUtils.newConfiguration(ConfigurationKind.JDK_ONLY, TestJdkKind.MOCK_JDK);

        for (File root : getKotlinSourceRoots()) {
            ContentRootsKt.addKotlinSourceRoot(configuration, root.getPath());
        }

        extraConfiguration(configuration);

        return KotlinCoreEnvironment.createForTests(getTestRootDisposable(), configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES);
    }

    protected void extraConfiguration(@NotNull CompilerConfiguration configuration) {
    }

    protected abstract List<File> getKotlinSourceRoots();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        KotlinCoreEnvironment environment = getEnvironment();
        KotlinTestUtils.resolveAllKotlinFiles(environment);
        finder = JavaElementFinder.Companion.getInstance(environment.getProject());
    }

    @Override
    protected void tearDown() throws Exception {
        finder = null;
        super.tearDown();
    }

}
