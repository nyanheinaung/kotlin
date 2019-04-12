/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.pcollections;

final class MapEntry<K, V> implements java.io.Serializable {
    private static final long serialVersionUID = 7138329143949025153L;

    public final K key;
    public final V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MapEntry)) return false;
        MapEntry<?, ?> e = (MapEntry<?, ?>) o;
        return (key == null ? e.key == null : key.equals(e.key)) &&
                (value == null ? e.value == null : value.equals(e.value));
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
