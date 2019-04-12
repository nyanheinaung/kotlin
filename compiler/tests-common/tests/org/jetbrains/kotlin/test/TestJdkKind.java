/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test;

public enum TestJdkKind {
    MOCK_JDK,
    // Differs from common mock JDK only by one additional 'nonExistingMethod' in Collection and constructor from Double in Throwable
    // It's needed to test the way we load additional built-ins members that neither in black nor white lists
    // Also, now it contains new methods in java.lang.String introduced in JDK 11
    MODIFIED_MOCK_JDK,
    // JDK found at $JDK_16
    FULL_JDK_6,
    // JDK found at $JDK_19
    FULL_JDK_9,
    // JDK found at java.home
    FULL_JDK,
    ANDROID_API,
}
