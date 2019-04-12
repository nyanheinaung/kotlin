/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.configure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.configuration.AbstractGradleConfigureProjectByChangingFileTest;

public abstract class AbstractConfigureProjectTest extends AbstractGradleConfigureProjectByChangingFileTest {
    public void doTestAndroidGradle(@NotNull String path) throws Exception {
         doTest(path, path.replace("before", "after"), new KotlinAndroidGradleModuleConfigurator());
    }
}
