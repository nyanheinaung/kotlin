/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public interface MapSupplier {

    MapSupplier HASH_MAP_SUPPLIER = new MapSupplier() {
        @Override
        public <K, V> Map<K, V> get() {
            return new HashMap<>();
        }
    };

    MapSupplier LINKED_HASH_MAP_SUPPLIER = new MapSupplier() {
        @Override
        public <K, V> Map<K, V> get() {
            return new LinkedHashMap<>();
        }
    };

    <K, V> Map<K, V> get();
}
