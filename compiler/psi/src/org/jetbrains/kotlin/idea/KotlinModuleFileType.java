/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class KotlinModuleFileType implements FileType {
    public static final String EXTENSION = "kotlin_module";
    public static final KotlinModuleFileType INSTANCE = new KotlinModuleFileType();

    private final NotNullLazyValue<Icon> myIcon = new NotNullLazyValue<Icon>() {
        @NotNull
        @Override
        protected Icon compute() {
            return KotlinFileIcon.KOTLIN_FILE;
        }
    };

    private KotlinModuleFileType() {}

    @Override
    @NotNull
    public String getName() {
        return EXTENSION;
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Kotlin module info: contains package part mappings";
    }

    @Override
    @NotNull
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return myIcon.getValue();
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }
}
