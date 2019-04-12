/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.ast;

import org.jetbrains.kotlin.js.backend.ast.JsName;
import org.jetbrains.kotlin.js.backend.ast.JsScope;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

public final class JsScopeTest extends TestCase {
    private JsScope scope;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        scope = new JsScope("Test scope") {};
    }

    public void testDeclareFreshName() throws Exception {
        declareFreshNameAndAssertEquals("a", "a");
        declareFreshNameAndAssertEquals("a", "a_0");
        declareFreshNameAndAssertEquals("a", "a_1");

        declareFreshNameAndAssertEquals("a_1", "a_2");
        declareFreshNameAndAssertEquals("a_3", "a_3");

        declareFreshNameAndAssertEquals("a_1_1", "a_1_1");
        declareFreshNameAndAssertEquals("a_1_1", "a_1_2");

        declareFreshNameAndAssertEquals("tmp$0", "tmp$0");
        declareFreshNameAndAssertEquals("tmp$0", "tmp$1");

        declareFreshNameAndAssertEquals("a0", "a0");
        declareFreshNameAndAssertEquals("a0", "a0_0");
        declareFreshNameAndAssertEquals("a0_0", "a0_1");
    }

    private void declareFreshNameAndAssertEquals(@NotNull String suggested, @NotNull String expected) {
        JsName actual = scope.declareFreshName(suggested);
        assertEquals(expected, actual.getIdent());
    }
}
