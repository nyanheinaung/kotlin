/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.storage

import java.util.concurrent.ConcurrentMap

abstract class ObservableStorageManager(private val delegate: StorageManager) : StorageManager {
    protected abstract val <T> (() -> T).observable: () -> T
    protected abstract val <K, V> ((K) -> V).observable: (K) -> V

    override fun <K, V: Any> createMemoizedFunction(compute: (K) -> V): MemoizedFunctionToNotNull<K, V> {
        return delegate.createMemoizedFunction(compute.observable)
    }

    override fun <K, V: Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V?): MemoizedFunctionToNullable<K, V> {
        return delegate.createMemoizedFunctionWithNullableValues(compute.observable)
    }

    override fun <K, V: Any> createMemoizedFunction(compute: (K) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNotNull<K, V> {
        return delegate.createMemoizedFunction(compute.observable, map)
    }

    override fun <K, V: Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNullable<K, V> {
        return delegate.createMemoizedFunctionWithNullableValues(compute.observable, map)
    }

    override fun <K, V : Any> createCacheWithNullableValues(): CacheWithNullableValues<K, V> {
        return delegate.createCacheWithNullableValues()
    }

    override fun <K, V : Any> createCacheWithNotNullValues(): CacheWithNotNullValues<K, V> {
        return delegate.createCacheWithNotNullValues()
    }

    override fun <T: Any> createLazyValue(computable: () -> T): NotNullLazyValue<T> {
        return delegate.createLazyValue(computable.observable)
    }

    override fun <T: Any> createRecursionTolerantLazyValue(computable: () -> T, onRecursiveCall: T): NotNullLazyValue<T> {
        return delegate.createRecursionTolerantLazyValue(computable.observable, onRecursiveCall)
    }

    override fun <T: Any> createLazyValueWithPostCompute(computable: () -> T, onRecursiveCall: ((Boolean) -> T)?, postCompute: (T) -> Unit): NotNullLazyValue<T> {
        return delegate.createLazyValueWithPostCompute(computable.observable, onRecursiveCall, postCompute)
    }

    override fun <T: Any> createNullableLazyValue(computable: () -> T?): NullableLazyValue<T> {
        return delegate.createNullableLazyValue(computable.observable)
    }

    override fun <T: Any> createRecursionTolerantNullableLazyValue(computable: () -> T?, onRecursiveCall: T?): NullableLazyValue<T> {
        return delegate.createRecursionTolerantNullableLazyValue(computable.observable, onRecursiveCall)
    }

    override fun <T: Any> createNullableLazyValueWithPostCompute(computable: () -> T?, postCompute: (T?) -> Unit): NullableLazyValue<T> {
        return delegate.createNullableLazyValueWithPostCompute(computable.observable, postCompute)
    }

    override fun <T> compute(computable: () -> T): T {
        return delegate.compute(computable.observable)
    }
}
