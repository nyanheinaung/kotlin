/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.semantics;

public final class WebDemoExamples1Test extends AbstractWebDemoExamplesTest {

    public WebDemoExamples1Test() {
        super("webDemoExamples1/");
    }

    public void testPrintArg() throws Exception {
        runMainAndCheckOutput("printArg.kt", "Hello, world!", "Hello, world!");
    }


    public void testWhileLoop() throws Exception {
        runMainAndCheckOutput("whileLoop.kt", "guest1\nguest2\nguest3\nguest4\n", "guest1", "guest2", "guest3", "guest4");
    }


    public void testIfAsExpression() throws Exception {
        runMainAndCheckOutput("ifAsExpression.kt", "20\n", "10", "20");
    }


    public void testObjectOrientedHello() throws Exception {
        runMainAndCheckOutput("objectOrientedHello.kt", "Hello, Pavel!\n", "Pavel");
    }


    public void testMultiLanguageHello() throws Exception {
        runMainAndCheckOutput("multiLanguageHello.kt", "Salut!\n", "FR");
    }


    public void testNullChecks() throws Exception {
        runMainAndCheckOutput("nullChecks.kt", "No number supplied");
        runMainAndCheckOutput("nullChecks.kt", "6", "2", "3");
    }


    public void testRanges() throws Exception {
        runMainAndCheckOutput("ranges.kt", "OK\n" +
                                           " 1 2 3 4 5\n" +
                                           "Out: array has only 3 elements. x = 4\n" +
                                           "Yes: array contains aaa\n" +
                                           "No: array doesn't contains ddd\n", "4");

        runMainAndCheckOutput("ranges.kt", " 1 2 3 4 5\n" +
                                           "Out: array has only 3 elements. x = 10\n" +
                                           "Yes: array contains aaa\n" +
                                           "No: array doesn't contains ddd\n", "10");
    }


    public void testForLoop() throws Exception {
        runMainAndCheckOutput("forLoop.kt", "a\n" +
                                            "b\n" +
                                            "c\n" +
                                            "\n" +
                                            "a\n" +
                                            "b\n" +
                                            "c\n", "a", "b", "c");
        runMainAndCheckOutput("forLoop.kt", "123\n\n123\n", "123");
    }


    public void testIsCheck() throws Exception {
        runMainAndCheckOutput("isCheck.kt", "3\nnull\n");
    }


    public void testPatternMatching() throws Exception {
        runMainAndCheckOutput("patternMatching.kt", "Greeting\n" +
                                                    "One\n" +
                                                    "Not a string\n" +
                                                    "Unknown\n");
    }
}
