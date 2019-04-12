/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests;

import java.util.HashSet;
import java.util.Set;

public class SpecialFiles {
    private static final Set<String> excludedFiles = new HashSet<>();

    static {
        fillExcludedFiles();
    }

    public static Set<String> getExcludedFiles() {
        return excludedFiles;
    }

    private static void fillExcludedFiles() {
        // Reflection
        excludedFiles.add("enclosing");
        excludedFiles.add("kt10259.kt");
        excludedFiles.add("simpleClassLiteral.kt");

        //UnsatisfiedLinkError
        excludedFiles.add("nativePropertyAccessors.kt");
        excludedFiles.add("topLevel.kt");

        //Test with no reflection at runtime
        excludedFiles.add("noReflectAtRuntime");
        excludedFiles.add("noReflect");
        excludedFiles.add("functionNtoStringNoReflect.kt");
        excludedFiles.add("getDelegateWithoutReflection.kt");

        // "IOOBE: Invalid index 4, size is 4" for java.lang.reflect.ParameterizedType on Android
        excludedFiles.add("innerGenericTypeArgument.kt");

        // Cannot change package name
        excludedFiles.add("kt6990.kt");
        excludedFiles.add("typeParameters.kt");
        excludedFiles.add("kt13133.kt");

        // StackOverflow with StringBuilder (escape())
        excludedFiles.add("kt684.kt");

        // Wrong enclosing info or signature after package renaming
        excludedFiles.add("enclosingInfo");
        excludedFiles.add("signature");

        // Some classes are not visible on android
        excludedFiles.add("classpath.kt");

        // Out of memory
        excludedFiles.add("manyNumbers.kt");

        // Native methods
        excludedFiles.add("external");

        // Additional nested class in 'Thread' class on Android
        excludedFiles.add("nestedClasses.kt");

        // KT-8120
        excludedFiles.add("closureOfInnerLocalClass.kt");
        excludedFiles.add("closureWithSelfInstantiation.kt");
        excludedFiles.add("quotedClassName.kt");

        //wrong function resolution after package renaming
        excludedFiles.add("apiVersionAtLeast1.kt");

        //special symbols in names
        excludedFiles.add("nameWithWhitespace.kt");
    }

    private SpecialFiles() {
    }
}
