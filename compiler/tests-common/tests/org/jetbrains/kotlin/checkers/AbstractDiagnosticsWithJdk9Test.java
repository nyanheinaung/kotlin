/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.test.ConfigurationKind;
import org.jetbrains.kotlin.test.TestJdkKind;

import java.io.File;

public abstract class AbstractDiagnosticsWithJdk9Test extends AbstractDiagnosticsTest {
    @NotNull
    @Override
    protected ConfigurationKind getConfigurationKind() {
        return ConfigurationKind.ALL;
    }

    @NotNull
    @Override
    protected TestJdkKind getTestJdkKind(@NotNull File file) {
        return TestJdkKind.FULL_JDK_9;
    }
}
