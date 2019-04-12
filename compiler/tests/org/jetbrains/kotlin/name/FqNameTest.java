/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FqNameTest {
    @Test
    public void pathSegments() {
        Assert.assertEquals(new ArrayList<Name>(), new FqName("").pathSegments());

        for (String name : new String[] { "org", "org.jetbrains", "org.jetbrains.kotlin" }) {
            List<Name> segments = new FqName(name).pathSegments();
            List<String> segmentsStrings = new ArrayList<>();
            for (Name segment : segments) {
                segmentsStrings.add(segment.asString());
            }
            Assert.assertEquals(Arrays.asList(name.split("\\.")), segmentsStrings);
        }
    }

    @Test
    public void safeUnsafe() {
        FqName fqName = new FqName("com.yandex");
        Assert.assertSame(fqName, fqName.toUnsafe().toSafe());
    }

    @Test
    public void unsafeSafe() {
        FqNameUnsafe fqName = new FqNameUnsafe("ru.yandex");
        Assert.assertSame(fqName, fqName.toSafe().toUnsafe());
    }

    @Test
    public void isValidJavaFqName() {
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName(""));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("a"));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("1"));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("a.a"));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("org.jetbrains"));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("$"));
        Assert.assertTrue(FqNamesUtilKt.isValidJavaFqName("org.A$B"));

        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("."));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName(".."));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a."));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName(".a"));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a..b"));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a.b.."));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a.b."));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a.b...)"));
        Assert.assertFalse(FqNamesUtilKt.isValidJavaFqName("a.b.<special>"));
    }
}
