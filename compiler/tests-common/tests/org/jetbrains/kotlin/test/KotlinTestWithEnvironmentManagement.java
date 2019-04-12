/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase;

public abstract class KotlinTestWithEnvironmentManagement extends KtUsefulTestCase {
    static {
        System.setProperty("java.awt.headless", "true");
    }

    @NotNull
    protected KotlinCoreEnvironment createEnvironmentWithMockJdk(@NotNull ConfigurationKind configurationKind) {
        return createEnvironmentWithJdk(configurationKind, TestJdkKind.MOCK_JDK);
    }

    @NotNull
    protected KotlinCoreEnvironment createEnvironmentWithJdk(@NotNull ConfigurationKind configurationKind, @NotNull TestJdkKind jdkKind) {
        return KotlinTestUtils.createEnvironmentWithJdkAndNullabilityAnnotationsFromIdea(getTestRootDisposable(), configurationKind, jdkKind);
    }
}
