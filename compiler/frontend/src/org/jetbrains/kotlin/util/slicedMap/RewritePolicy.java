/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util.slicedMap;

public interface RewritePolicy {

    RewritePolicy DO_NOTHING = new RewritePolicy() {
        @Override
        public <K> boolean rewriteProcessingNeeded(K key) {
            return false;
        }

        @Override
        public <K, V> boolean processRewrite(WritableSlice<K, V> slice, K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }
    };

    <K> boolean rewriteProcessingNeeded(K key);

    // True to put, false to skip
    <K, V> boolean processRewrite(WritableSlice<K, V> slice, K key, V oldValue, V newValue);
}
