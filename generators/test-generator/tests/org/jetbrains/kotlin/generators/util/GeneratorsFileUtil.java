/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.util;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import kotlin.io.FilesKt;
import kotlin.text.Charsets;
import org.jetbrains.kotlin.test.KotlinTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GeneratorsFileUtil {
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void writeFileIfContentChanged(File file, String newText) throws IOException {
        writeFileIfContentChanged(file, newText, true);
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void writeFileIfContentChanged(File file, String newText, boolean logNotChanged) throws IOException {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            if (parentFile.mkdirs()) {
                System.out.println("Directory created: " + parentFile.getAbsolutePath());
            }
            else {
                throw new IllegalStateException("Cannot create directory: " + parentFile);
            }
        }

        if (checkFileIgnoringLineSeparators(file, newText)) {
            if (logNotChanged) {
                System.out.println("Not changed: " + file.getAbsolutePath());
            }
            return;
        }

        boolean useTempFile = !SystemInfo.isWindows;

        File tempFile = useTempFile ? new File(KotlinTestUtils.tmpDir(file.getName()), file.getName() + ".tmp") : file;

        FilesKt.writeText(tempFile, newText, Charsets.UTF_8);
        System.out.println("File written: " + tempFile.getAbsolutePath());
        if (useTempFile) {
            Files.move(tempFile.toPath(), file.toPath(), REPLACE_EXISTING);
            System.out.println("Renamed " + tempFile + " to " + file);
        }
        System.out.println();
    }

    private static boolean checkFileIgnoringLineSeparators(File file, String content) {
        String currentContent;
        try {
            currentContent = StringUtil.convertLineSeparators(FilesKt.readText(file, Charsets.UTF_8));
        }
        catch (Throwable ignored) {
            return false;
        }

        return StringUtil.convertLineSeparators(content).equals(currentContent);
    }
}
