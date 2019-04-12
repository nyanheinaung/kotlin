/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.annotation;

import java.io.IOException;

public class AnnotationDescriptorResolveTest extends AbstractAnnotationDescriptorResolveTest {
    public void testIntAnnotation() throws IOException {
        String content = getContent("AnnInt(1)");
        String expectedAnnotation = "@AnnInt(a = 1)";
        doTest(content, expectedAnnotation);
    }

    public void testStringAnnotation() throws IOException {
        String content = getContent("AnnString(\"test\")");
        String expectedAnnotation = "@AnnString(a = \"test\")";
        doTest(content, expectedAnnotation);
    }

    public void testEnumAnnotation() throws IOException {
        String content = getContent("AnnEnum(MyEnum.A)");
        String expectedAnnotation = "@AnnEnum(a = MyEnum.A)";
        doTest(content, expectedAnnotation);
    }

    public void testQualifiedEnumAnnotation() throws IOException {
        String content = getContent("AnnEnum(MyEnum.A)");
        String expectedAnnotation = "@AnnEnum(a = MyEnum.A)";
        doTest(content, expectedAnnotation);
    }

    public void testUnqualifiedEnumAnnotation() throws IOException {
        String content = getContent("AnnEnum(A)");
        String expectedAnnotation = "@AnnEnum(a = MyEnum.A)";
        doTest(content, expectedAnnotation);
    }

    public void testIntArrayAnnotation() throws IOException {
        String content = getContent("AnnIntArray(intArrayOf(1, 2))");
        String expectedAnnotation = "@AnnIntArray(a = {1, 2})";
        doTest(content, expectedAnnotation);
    }

    public void testIntArrayVarargAnnotation() throws IOException {
        String content = getContent("AnnIntVararg(1, 2)");
        String expectedAnnotation = "@AnnIntVararg(a = {1, 2})";
        doTest(content, expectedAnnotation);
    }

    public void testStringArrayVarargAnnotation() throws IOException {
        String content = getContent("AnnStringVararg(\"a\", \"b\")");
        String expectedAnnotation = "@AnnStringVararg(a = {\"a\", \"b\"})";
        doTest(content, expectedAnnotation);
    }

    public void testStringArrayAnnotation() throws IOException {
        String content = getContent("AnnStringArray(arrayOf(\"a\"))");
        String expectedAnnotation = "@AnnStringArray(a = {\"a\"})";
        doTest(content, expectedAnnotation);
    }

    public void testEnumArrayAnnotation() throws IOException {
        String content = getContent("AnnArrayOfEnum(arrayOf(MyEnum.A))");
        String expectedAnnotation = "@AnnArrayOfEnum(a = {MyEnum.A})";
        doTest(content, expectedAnnotation);
    }

    public void testAnnotationAnnotation() throws Exception {
        String content = getContent("AnnAnn(AnnInt(1))");
        String expectedAnnotation = "@AnnAnn(a = AnnInt(a = 1))";
        doTest(content, expectedAnnotation);
    }

    public void testJavaClassAnnotation() throws Exception {
        String content = getContent("AnnClass(MyClass::class)");
        String expectedAnnotation = "@AnnClass(a = test.MyClass::class)";
        doTest(content, expectedAnnotation);
    }
}
