/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.junit.Test
import kotlin.test.assertEquals

class GradleVersionTest {
    @Test
    fun testParse() {
        fun String.parseToPair() = ParsedGradleVersion.parse(this)?.let { it.major to it.minor }

        assertEquals(2 to 1, "2.1".parseToPair())
        assertEquals(2 to 10, "2.10".parseToPair())
        assertEquals(2 to 14, "2.14.1".parseToPair())
        assertEquals(3 to 2, "3.2-rc-1".parseToPair())
        assertEquals(3 to 2, "3.2".parseToPair())
        assertEquals(4 to 0, "4.0".parseToPair())
    }

    @Test
    fun testCompare() {
        assert(ParsedGradleVersion(3, 2) == ParsedGradleVersion(3, 2))
        assert(ParsedGradleVersion(3, 2) > ParsedGradleVersion(2, 14))
        assert(ParsedGradleVersion(3, 2) > ParsedGradleVersion(3, 1))
        assert(ParsedGradleVersion(3, 2) < ParsedGradleVersion(3, 3))
        assert(ParsedGradleVersion(4, 0) > ParsedGradleVersion(3, 3))
    }
}