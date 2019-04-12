/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;

public class OutputMessageUtil {
    private static final String SOURCE_FILES_PREFIX = "Sources:";
    private static final String OUTPUT_FILES_PREFIX = "Output:";

    @NotNull
    public static String renderException(@NotNull Throwable e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

    @NotNull
    public static String formatOutputMessage(Collection<File> sourceFiles, File outputFile) {
        return OUTPUT_FILES_PREFIX + "\n" + outputFile.getPath() + "\n" +
               SOURCE_FILES_PREFIX + "\n" + StringUtil.join(sourceFiles, "\n");
    }

    @Nullable
    public static Output parseOutputMessage(@NotNull String message) {
        String[] strings = message.split("\n");

        // Must have at least one line per prefix
        if (strings.length <= 2) return null;

        if (!OUTPUT_FILES_PREFIX.equals(strings[0])) return null;

        if (SOURCE_FILES_PREFIX.equals(strings[1])) {
            // Output:
            // Sources:
            // ...
            return new Output(parseSourceFiles(strings, 2), null);
        }
        else {
            File outputFile = new File(strings[1]);

            if (!SOURCE_FILES_PREFIX.equals(strings[2])) return null;

            return new Output(parseSourceFiles(strings, 3), outputFile);
        }
    }

    private static Collection<File> parseSourceFiles(String[] strings, int start) {
        Collection<File> sourceFiles = ContainerUtil.newArrayList();
        for (int i = start; i < strings.length; i++) {
            sourceFiles.add(new File(strings[i]));
        }
        return sourceFiles;
    }

    public static class Output implements Serializable {
        @NotNull
        public final Collection<File> sourceFiles;
        @Nullable
        public final File outputFile;

        public Output(@NotNull Collection<File> sourceFiles, @Nullable File outputFile) {
            this.sourceFiles = sourceFiles;
            this.outputFile = outputFile;
        }

        static final long serialVersionUID = 0L;
    }
}
