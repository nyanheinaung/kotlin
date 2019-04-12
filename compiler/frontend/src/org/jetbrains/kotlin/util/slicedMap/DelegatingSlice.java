/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap;

import org.jetbrains.annotations.NotNull;

/**
 * Do nothing but dispatching all invokes to internal writable slice.
 */
public class DelegatingSlice<K, V> implements WritableSlice<K, V> {
    private final WritableSlice<K, V> delegate;

    public DelegatingSlice(@NotNull WritableSlice<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isCollective() {
        return delegate.isCollective();
    }

    @Override
    public boolean check(K key, V value) {
        return delegate.check(key, value);
    }

    @Override
    public void afterPut(MutableSlicedMap map, K key, V value) {
        delegate.afterPut(map, key, value);
    }

    @Override
    public RewritePolicy getRewritePolicy() {
        return delegate.getRewritePolicy();
    }

    @Override
    @NotNull
    public KeyWithSlice<K, V, WritableSlice<K, V>> getKey() {
        return delegate.getKey();
    }

    @Override
    public V computeValue(SlicedMap map, K key, V value, boolean valueNotFound) {
        return delegate.computeValue(map, key, value, valueNotFound);
    }

    @Override
    public ReadOnlySlice<K, V> makeRawValueVersion() {
        return delegate.makeRawValueVersion();
    }
}
