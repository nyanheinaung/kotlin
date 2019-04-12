/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap;

import org.jetbrains.annotations.NotNull;

public interface ReadOnlySlice<K, V> {
    @NotNull
    KeyWithSlice<K, V, ? extends ReadOnlySlice<K, V>> getKey();

    V computeValue(SlicedMap map, K key, V value, boolean valueNotFound);

    /**
     * @return a slice that only retrieves the value from the storage and skips any computeValue() calls
     */
    ReadOnlySlice<K, V> makeRawValueVersion();
}
