/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.test.KotlinTestUtils;

import java.io.File;

/**
 * Stub for com.android.testutils.TestUtils
 * stabbed to minimize changes in AndroidTestBase
 */
public class TestUtils {
    @NotNull
    public static File getSdk() {
        return KotlinTestUtils.findAndroidSdk();
    }

    @NotNull
    public static String getLatestAndroidPlatform() {
        return "android-26";
    }
}
