/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.semantics;

public final class WebDemoExamples2Test extends AbstractWebDemoExamplesTest {

    public WebDemoExamples2Test() {
        super("webDemoExamples2/");
    }

    public void testBottles() throws Exception {
        runMainAndCheckOutputWithExpectedFile("bottles", "2", "2");
        runMainAndCheckOutputWithExpectedFile("bottles", "");
    }

    public void testLife() throws Exception {
        runMainAndCheckOutputWithExpectedFile("life", "", "2");
    }

    public void testBuilder() throws Exception {
        runMainAndCheckOutputWithExpectedFile("builder", "");
        runMainAndCheckOutputWithExpectedFile("builder", "1", "over9000");
    }
}
