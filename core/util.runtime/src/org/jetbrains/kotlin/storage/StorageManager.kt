/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.storage

import java.util.concurrent.ConcurrentMap

interface StorageManager {
    /**
     * Given a function compute: K -> V create a memoized version of it that computes a value only once for each key
     * @param compute the function to be memoized
     * @param valuesReferenceKind how to store the memoized values
     *
     * NOTE: if compute() has side-effects the WEAK reference kind is dangerous: the side-effects will be repeated if
     *       the value gets collected and then re-computed
     */
    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V?): MemoizedFunctionToNullable<K, V>

    fun <K, V : Any> createCacheWithNullableValues(): CacheWithNullableValues<K, V>
    fun <K, V : Any> createCacheWithNotNullValues(): CacheWithNotNullValues<K, V>

    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNullable<K, V>

    fun <T : Any> createLazyValue(computable: () -> T): NotNullLazyValue<T>

    fun <T : Any> createRecursionTolerantLazyValue(computable: () -> T, onRecursiveCall: T): NotNullLazyValue<T>

    /**
     * @param onRecursiveCall is called if the computation calls itself recursively.
     *                        The parameter to it is {@code true} for the first call, {@code false} otherwise.
     *                        If {@code onRecursiveCall} is {@code null}, an exception will be thrown on a recursive call,
     *                        otherwise it's executed and its result is returned
     *
     * @param postCompute is called after the value is computed AND published (and some clients rely on that
     *                    behavior - notably, AbstractTypeConstructor). It means that it is up to particular implementation
     *                    to provide (or not to provide) thread-safety guarantees on writes made in postCompute -- see javadoc for
     *                    LockBasedLazyValue for details.
     */
    fun <T : Any> createLazyValueWithPostCompute(computable: () -> T, onRecursiveCall: ((Boolean) -> T)?, postCompute: (T) -> Unit): NotNullLazyValue<T>

    fun <T : Any> createNullableLazyValue(computable: () -> T?): NullableLazyValue<T>

    fun <T : Any> createRecursionTolerantNullableLazyValue(computable: () -> T?, onRecursiveCall: T?): NullableLazyValue<T>

    /**
     * See javadoc for createLazyValueWithPostCompute
     */
    fun <T : Any> createNullableLazyValueWithPostCompute(computable: () -> T?, postCompute: (T?) -> Unit): NullableLazyValue<T>

    fun <T> compute(computable: () -> T): T
}
