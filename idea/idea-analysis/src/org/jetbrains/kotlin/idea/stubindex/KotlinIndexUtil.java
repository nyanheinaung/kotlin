/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.stubindex;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class KotlinIndexUtil {
    @NotNull
    public static <K, Psi extends PsiElement> StubIndexKey<K, Psi> createIndexKey(@NotNull Class<? extends StubIndexExtension<K, Psi>> indexClass) {
        return StubIndexKey.createIndexKey(indexClass.getCanonicalName());
    }

    private KotlinIndexUtil() {}
}
