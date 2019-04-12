/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:JvmName("CollectionsJDK8Kt")
@file:kotlin.jvm.JvmPackageName("kotlin.collections.jdk8")

package kotlin.collections

/**
 * Returns the value to which the specified key is mapped, or
 * [defaultValue] if this map contains no mapping for the key.
 */
@SinceKotlin("1.2")
@kotlin.internal.InlineOnly
public inline fun <@kotlin.internal.OnlyInputTypes K, V> Map<out K, V>.getOrDefault(key: K, defaultValue: V): V =
    (this as Map<K, V>).getOrDefault(key, defaultValue)


/**
 * Removes the entry for the specified key only if it is currently
 * mapped to the specified value.
 */
@SinceKotlin("1.2")
@kotlin.internal.InlineOnly
public inline fun <@kotlin.internal.OnlyInputTypes K, @kotlin.internal.OnlyInputTypes V> MutableMap<out K, out V>.remove(key: K, value: V): Boolean =
    (this as MutableMap<K, V>).remove(key, value)
