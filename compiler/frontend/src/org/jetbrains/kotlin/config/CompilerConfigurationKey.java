/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config;

import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CompilerConfigurationKey<T> {
    Key<T> ideaKey;

    public CompilerConfigurationKey(@NotNull @NonNls String name) {
        ideaKey = Key.create(name);
    }

    @NotNull
    public static <T> CompilerConfigurationKey<T> create(@NotNull @NonNls String name) {
        return new CompilerConfigurationKey<>(name);
    }

    @Override
    public String toString() {
        return ideaKey.toString();
    }
}
