/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

public class IncrementalCompilation {
    public static final String INCREMENTAL_COMPILATION_JVM_PROPERTY = "kotlin.incremental.compilation";
    public static final String INCREMENTAL_COMPILATION_JS_PROPERTY = "kotlin.incremental.compilation.js";

    public static boolean isEnabledForJvm() {
        return "true".equals(System.getProperty(INCREMENTAL_COMPILATION_JVM_PROPERTY));
    }

    public static boolean isEnabledForJs() {
        return "true".equals(System.getProperty(INCREMENTAL_COMPILATION_JS_PROPERTY));
    }

    @TestOnly
    public static void setIsEnabledForJvm(boolean value) {
        System.setProperty(INCREMENTAL_COMPILATION_JVM_PROPERTY, String.valueOf(value));
    }

    @TestOnly
    public static void setIsEnabledForJs(boolean value) {
        System.setProperty(INCREMENTAL_COMPILATION_JS_PROPERTY, String.valueOf(value));
    }

    public static void toJvmArgs(List<String> jvmArgs) {
        if (isEnabledForJvm()) addJvmSystemFlag(jvmArgs, INCREMENTAL_COMPILATION_JVM_PROPERTY);
        if (isEnabledForJs()) addJvmSystemFlag(jvmArgs, INCREMENTAL_COMPILATION_JS_PROPERTY);
    }

    private static void addJvmSystemFlag(List<String> jvmArgs, String name) {
        jvmArgs.add("D" + name + "=true");
    }
}