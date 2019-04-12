/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test;

import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase;

import java.io.File;

public abstract class TestCaseWithTmpdir extends KtUsefulTestCase {
    protected File tmpdir;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tmpdir = KotlinTestUtils.tmpDirForTest(this);
    }
}
