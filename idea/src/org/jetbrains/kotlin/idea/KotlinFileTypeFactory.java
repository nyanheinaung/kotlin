/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class KotlinFileTypeFactory extends FileTypeFactory {
    public final static String[] KOTLIN_EXTENSIONS = new String[] { "kt", "kts" };
    private final static FileType[] KOTLIN_FILE_TYPES = new FileType[] { KotlinFileType.INSTANCE };
    public final static Set<FileType> KOTLIN_FILE_TYPES_SET = new HashSet<>(Arrays.asList(KOTLIN_FILE_TYPES));

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(KotlinFileType.INSTANCE, "kt;kts");
    }
}
