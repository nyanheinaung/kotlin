/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.jvm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.test.testFramework.KtUsefulTestCase;

import java.util.Random;

import static org.jetbrains.kotlin.metadata.jvm.deserialization.BitEncoding.decodeBytes;
import static org.jetbrains.kotlin.metadata.jvm.deserialization.BitEncoding.encodeBytes;
import static org.junit.Assert.assertArrayEquals;

public class BitEncodingTest extends KtUsefulTestCase {
    private static final int[] BIG_LENGTHS = new int[]
            {1000, 32000, 33000, 65000, 65534, 65535, 65536, 65537, 100000, 131074, 239017, 314159, 1000000};

    private static void doTest(int randSeed, int length) throws Exception {
        byte[] a = new byte[length];
        new Random(randSeed).nextBytes(a);

        String[] b = encodeBytes(a);
        for (String string : b) {
            assertStringConformsToJVMS(string);
        }

        byte[] c = decodeBytes(b);
        String message = "Failed randSeed = " + randSeed + ", length = " + length;
        assertArrayEquals(message, a, c);

        String[] d = encodeBytes(c);
        assertArrayEquals(message, b, d);

        byte[] e = decodeBytes(d);
        assertArrayEquals(message, a, e);

    }

    private static void assertStringConformsToJVMS(@NotNull String string) {
        int effectiveLength = string.length();
        for (char c : string.toCharArray()) {
            if (c == 0x0) effectiveLength++;
        }
        assertTrue(String.format("String exceeds maximum allowed length in a class file: %d > 65535", effectiveLength),
                   effectiveLength <= 65535);
    }

    public void testEncodeDecode() throws Exception {
        for (int length = 0; length <= 100; length++) {
            for (int randSeed = 1; randSeed <= 100; randSeed++) {
                doTest(randSeed, length);
            }
        }

        for (int length : BIG_LENGTHS) {
            for (int randSeed = 1; randSeed <= 3; randSeed++) {
                doTest(randSeed, length);
            }
        }
    }
}
