/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.pcollections;

/**
 * An efficient persistent map from integer keys to non-null values.
 */
final class IntTreePMap<V> {
    private static final IntTreePMap<Object> EMPTY = new IntTreePMap<Object>(IntTree.EMPTYNODE);

    @SuppressWarnings("unchecked")
    public static <V> IntTreePMap<V> empty() {
        return (IntTreePMap<V>) EMPTY;
    }

    private final IntTree<V> root;

    private IntTreePMap(IntTree<V> root) {
        this.root = root;
    }

    private IntTreePMap<V> withRoot(IntTree<V> root) {
        if (root == this.root) return this;
        return new IntTreePMap<V>(root);
    }

    public V get(int key) {
        return root.get(key);
    }

    public IntTreePMap<V> plus(int key, V value) {
        return withRoot(root.plus(key, value));
    }

    public IntTreePMap<V> minus(int key) {
        return withRoot(root.minus(key));
    }
}
