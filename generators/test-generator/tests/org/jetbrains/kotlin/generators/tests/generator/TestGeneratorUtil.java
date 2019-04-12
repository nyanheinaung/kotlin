/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.tests.generator;

import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class TestGeneratorUtil {
    @NotNull
    public static String escapeForJavaIdentifier(String fileName) {
        // A file name may contain characters (like ".") that can't be a part of method name
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);

            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
            }
            else {
                result.append("_");
            }
        }
        return result.toString();
    }

    @NotNull
    public static String fileNameToJavaIdentifier(@NotNull File file) {
        return StringsKt.capitalize(escapeForJavaIdentifier(file.getName()));
    }
}
