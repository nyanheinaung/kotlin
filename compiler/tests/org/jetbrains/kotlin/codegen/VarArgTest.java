/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.kotlin.test.ConfigurationKind;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VarArgTest extends CodegenTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createEnvironmentWithMockJdkAndIdeaAnnotations(ConfigurationKind.JDK_ONLY);
    }

    public void testStringArray() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test(vararg ts: String) = ts");
        Method main = generateFunction();
        String[] args = {"mama", "papa"};
        assertTrue(args == main.invoke(null, new Object[]{ args } ));
    }

    public void testIntArray() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test(vararg ts: Int) = ts");
        Method main = generateFunction();
        int[] args = {3, 4};
        assertTrue(args == main.invoke(null, new Object[]{ args }));
    }

    public void testIntArrayKotlinNoArgs() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test() = testf(); fun testf(vararg ts: Int) = ts");
        Method main = generateFunction("test");
        Object res = main.invoke(null);
        assertTrue(((int[])res).length == 0);
    }

    public void testIntArrayKotlin() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test() = testf(239, 7); fun testf(vararg ts: Int) = ts");
        Method main = generateFunction("test");
        Object res = main.invoke(null);
        assertTrue(((int[])res).length == 2);
        assertTrue(((int[])res)[0] == 239);
        assertTrue(((int[])res)[1] == 7);
    }

    public void testNullableIntArrayKotlin() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test() = testf(239.toByte(), 7.toByte()); fun testf(vararg ts: Byte?) = ts");
        Method main = generateFunction("test");
        Object res = main.invoke(null);
        assertTrue(((Byte[])res).length == 2);
        assertTrue(((Byte[])res)[0] == (byte)239);
        assertTrue(((Byte[])res)[1] == 7);
    }

    public void testIntArrayKotlinObj() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test() = testf(\"239\"); fun testf(vararg ts: String) = ts");
        Method main = generateFunction("test");
        Object res = main.invoke(null);
        assertTrue(((String[])res).length == 1);
        assertTrue(((String[])res)[0].equals("239"));
    }

    public void testArrayT() throws InvocationTargetException, IllegalAccessException {
        loadText("fun test() = _array(2, 4); fun <T> _array(vararg elements : T) = elements");
        Method main = generateFunction("test");
        Object res = main.invoke(null);
        assertTrue(((Integer[])res).length == 2);
        assertTrue(((Integer[])res)[0].equals(2));
        assertTrue(((Integer[])res)[1].equals(4));
    }

    public void testArrayAsVararg() throws InvocationTargetException, IllegalAccessException {
        loadText("private fun asList(vararg elems: String) = elems; fun test(ts: Array<String>) = asList(*ts); ");
        Method main = generateFunction("test");
        String[] args = {"mama", "papa"};
        String[] result = (String []) main.invoke(null, new Object[] {args});
        assertTrue(args != result);
        assertTrue(Arrays.equals(args, result));
    }

    public void testArrayAsVararg2() throws InvocationTargetException, IllegalAccessException {
        loadText("private fun asList(vararg elems: String) = elems; fun test(ts1: Array<String>, ts2: String) = asList(*ts1, ts2); ");
        Method main = generateFunction("test");
        Object invoke = main.invoke(null, new String[] {"mama"}, "papa");
        assertInstanceOf(invoke, String[].class);
        assertEquals(2, Array.getLength(invoke));
        assertEquals("mama", Array.get(invoke, 0));
        assertEquals("papa", Array.get(invoke, 1));
    }
}
