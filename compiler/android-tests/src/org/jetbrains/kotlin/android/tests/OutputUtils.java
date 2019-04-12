/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.tests;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.android.tests.run.RunResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputUtils {

    private final static Pattern EMULATOR_PROCESS_PATTERN = Pattern.compile("\\w*[\\s]+([0-9]*) .* java .* emulator .*");

    public static boolean isBuildFailed(String output) {
        return output.contains("BUILD FAILED") || output.contains("Build failed");
    }

    public static void checkResult(RunResult result) {
        if (!result.getStatus()) {
            throw new RuntimeException(result.getOutput());
        }
    }

    @Nullable
    public static String getPidFromPsCommand(String output) {
        if (!output.isEmpty()) {
            Matcher matcher = EMULATOR_PROCESS_PATTERN.matcher(output);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private OutputUtils() {
    }
}
