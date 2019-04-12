/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.storage;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.BindingTrace;

public interface LazyResolveStorageManager extends StorageManager {
    @NotNull
    <K, V> MemoizedFunctionToNotNull<K, V> createSoftlyRetainedMemoizedFunction(@NotNull Function1<K, V> compute);

    @NotNull
    <K, V> MemoizedFunctionToNullable<K, V> createSoftlyRetainedMemoizedFunctionWithNullableValues(@NotNull Function1<K, V> compute);

    @NotNull
    BindingTrace createSafeTrace(@NotNull BindingTrace originalTrace);
}
