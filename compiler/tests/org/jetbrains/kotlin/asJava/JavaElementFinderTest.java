/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava;

import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import junit.framework.TestCase;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class JavaElementFinderTest extends KotlinAsJavaTestBase {

    @Override
    protected List<File> getKotlinSourceRoots() {
        return Collections.singletonList(
                new File("compiler/testData/asJava/findClasses/" + getTestName(false) + ".kt")
        );
    }

    @Override
    protected void tearDown() throws Exception {
        finder = null;
        super.tearDown();
    }

    public void testFromEnumEntry() {
        assertClass("Direction");
        assertNoClass("Direction.NORTH");
        assertNoClass("Direction.SOUTH");
        assertNoClass("Direction.WEST");
        // TODO: assertClass("Direction.SOUTH.Hello");
        // TODO: assertClass("Direction.WEST.Some");
    }

    public void testEmptyQualifiedName() {
        assertNoClass("");
    }

    private void assertClass(String qualifiedName) {
        PsiClass psiClass = finder.findClass(qualifiedName, GlobalSearchScope.allScope(getProject()));
        TestCase.assertNotNull(String.format("Class with fqn='%s' wasn't found.", qualifiedName), psiClass);
        TestCase.assertTrue(String.format("Class with fqn='%s' is not valid.", qualifiedName), psiClass.isValid());
    }

    private void assertNoClass(String qualifiedName) {
        TestCase.assertNull(String.format("Class with fqn='%s' isn't expected to be found.", qualifiedName),
                            finder.findClass(qualifiedName, GlobalSearchScope.allScope(getProject())));
    }
}
