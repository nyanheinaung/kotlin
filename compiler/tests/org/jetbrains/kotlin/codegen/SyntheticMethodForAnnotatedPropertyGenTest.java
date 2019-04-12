/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.load.java.JvmAbi;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.test.ConfigurationKind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SyntheticMethodForAnnotatedPropertyGenTest extends CodegenTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createEnvironmentWithMockJdkAndIdeaAnnotations(ConfigurationKind.JDK_ONLY);
    }

    @NotNull
    @Override
    protected String getPrefix() {
        return "properties/syntheticMethod";
    }

    private static final String TEST_SYNTHETIC_METHOD_NAME = JvmAbi.getSyntheticMethodNameForAnnotatedProperty(Name.identifier("property"));

    public void testInClass() {
        loadFile();
        assertAnnotatedSyntheticMethodExistence(true, generateClass("A"));
    }

    public void testTopLevel() {
        loadFile();
        Class<?> a = generateClass("TopLevelKt");
        assertAnnotatedSyntheticMethodExistence(true, a);
    }

    public void testInTrait() throws ClassNotFoundException {
        loadFile();
        GeneratedClassLoader loader = generateAndCreateClassLoader();
        assertAnnotatedSyntheticMethodExistence(false, loader.loadClass("T"));
        assertAnnotatedSyntheticMethodExistence(true, loader.loadClass("T" + JvmAbi.DEFAULT_IMPLS_SUFFIX));
    }

    private static void assertAnnotatedSyntheticMethodExistence(boolean expected, @NotNull Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (TEST_SYNTHETIC_METHOD_NAME.equals(method.getName())) {
                if (!expected) {
                    fail("Synthetic method for annotated property found, but not expected: " + method);
                }
                assertTrue(method.isSynthetic());
                int modifiers = method.getModifiers();
                assertTrue(Modifier.isStatic(modifiers));
                assertTrue(Modifier.isPublic(modifiers));

                Annotation[] annotations = method.getDeclaredAnnotations();
                assertSize(1, annotations);
                assertEquals("@SomeAnnotation(value=OK)", annotations[0].toString());
                return;
            }
        }
        if (expected) {
            fail("Synthetic method for annotated property expected, but not found: " + TEST_SYNTHETIC_METHOD_NAME);
        }
    }
}
