/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap;

import org.jetbrains.annotations.NotNull;

public interface WritableSlice<K, V> extends ReadOnlySlice<K, V> {
    @NotNull
    @Override
    KeyWithSlice<K, V, WritableSlice<K, V>> getKey();

    // True to put, false to skip
    boolean check(K key, V value);

    void afterPut(MutableSlicedMap map, K key, V value);

    RewritePolicy getRewritePolicy();

    // In a sliced map one can request all keys for a collective slice
    boolean isCollective();
}
