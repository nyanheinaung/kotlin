/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg

import org.jetbrains.kotlin.util.javaslang.ImmutableHashMap
import org.jetbrains.kotlin.util.javaslang.ImmutableMap

interface ReadOnlyControlFlowInfo<K : Any, D : Any> {
    fun getOrNull(key: K): D?
    // Only used in tests
    fun asMap(): ImmutableMap<K, D>
}

abstract class ControlFlowInfo<S : ControlFlowInfo<S, K, D>, K : Any, D : Any>
internal constructor(
    protected val map: ImmutableMap<K, D> = ImmutableHashMap.empty()
) : ImmutableMap<K, D> by map, ReadOnlyControlFlowInfo<K, D> {
    protected abstract fun copy(newMap: ImmutableMap<K, D>): S

    override fun put(key: K, value: D): S = put(key, value, this[key].getOrElse(null as D?))

    /**
     * This overload exists just for sake of optimizations: in some cases we've just retrieved the old value,
     * so we don't need to scan through the persistent hashmap again
     */
    fun put(key: K, value: D, oldValue: D?): S {
        @Suppress("UNCHECKED_CAST")
        // Avoid a copy instance creation if new value is the same
        if (value == oldValue) return this as S
        return copy(map.put(key, value))
    }

    override fun getOrNull(key: K): D? = this[key].getOrElse(null as D?)
    override fun asMap() = this

    fun retainAll(predicate: (K) -> Boolean): S = copy(map.removeAll(map.keySet().filterNot(predicate)))

    override fun equals(other: Any?) = map == (other as? ControlFlowInfo<*, *, *>)?.map

    override fun hashCode() = map.hashCode()

    override fun toString() = map.toString()
}