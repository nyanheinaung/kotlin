/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:JvmName("StreamsKt")
@file:kotlin.jvm.JvmPackageName("kotlin.streams.jdk8")
package kotlin.streams

import java.util.*
import java.util.stream.*

/**
 * Creates a [Sequence] instance that wraps the original stream iterating through its elements.
 */
@SinceKotlin("1.2")
public fun <T> Stream<T>.asSequence(): Sequence<T> = Sequence { iterator() }

/**
 * Creates a [Sequence] instance that wraps the original stream iterating through its elements.
 */
@SinceKotlin("1.2")
public fun IntStream.asSequence(): Sequence<Int> = Sequence { iterator() }

/**
 * Creates a [Sequence] instance that wraps the original stream iterating through its elements.
 */
@SinceKotlin("1.2")
public fun LongStream.asSequence(): Sequence<Long> = Sequence { iterator() }

/**
 * Creates a [Sequence] instance that wraps the original stream iterating through its elements.
 */
@SinceKotlin("1.2")
public fun DoubleStream.asSequence(): Sequence<Double> = Sequence { iterator() }

/**
 * Creates a sequential [Stream] instance that produces elements from the original sequence.
 */
@SinceKotlin("1.2")
public fun <T> Sequence<T>.asStream(): Stream<T> =
    StreamSupport.stream({ Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED) }, Spliterator.ORDERED, false)

/**
 * Returns a [List] containing all elements produced by this stream.
 */
@SinceKotlin("1.2")
public fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList<T>())

/**
 * Returns a [List] containing all elements produced by this stream.
 */
@SinceKotlin("1.2")
public fun IntStream.toList(): List<Int> = toArray().asList()

/**
 * Returns a [List] containing all elements produced by this stream.
 */
@SinceKotlin("1.2")
public fun LongStream.toList(): List<Long> = toArray().asList()

/**
 * Returns a [List] containing all elements produced by this stream.
 */
@SinceKotlin("1.2")
public fun DoubleStream.toList(): List<Double> = toArray().asList()
