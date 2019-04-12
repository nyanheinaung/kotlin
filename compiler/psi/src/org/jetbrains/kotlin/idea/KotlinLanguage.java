/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class KotlinLanguage extends Language {
    @NotNull
    public static final KotlinLanguage INSTANCE = new KotlinLanguage();
    public static final String NAME = "Kotlin";

    private KotlinLanguage() {
        super("kotlin");
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
