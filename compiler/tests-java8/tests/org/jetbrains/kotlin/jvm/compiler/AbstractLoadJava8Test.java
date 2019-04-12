/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.test.TestJdkKind;

public abstract class AbstractLoadJava8Test extends AbstractLoadJavaTest {
    @NotNull
    @Override
    protected TestJdkKind getJdkKind() {
        return TestJdkKind.FULL_JDK;
    }
}
