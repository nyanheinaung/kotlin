/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util

import java.util.Collections
import java.util.IdentityHashMap

fun <T> IdentitySet(): MutableSet<T> {
    return Collections.newSetFromMap(IdentityHashMap<T, Boolean>())
}

fun <T> Collection<T>.toIdentitySet(): MutableSet<T> {
    val result = IdentitySet<T>()
    for (element in this) {
        result.add(element)
    }

    return result
}

fun <T> Sequence<T>.toIdentitySet(): MutableSet<T> {
    val result = IdentitySet<T>()
    for (element in this) {
        result.add(element)
    }

    return result
}

fun <T, R> Iterable<T>.zipWithDefault(
        other: Iterable<R>,
        defaultT: T
): List<Pair<T, R>> {

    val itT = iterator()
    val itR = other.iterator()

    val result = arrayListOf<Pair<T, R>>()

    while (itT.hasNext() && itR.hasNext()) {
        result.add(itT.next() to itR.next())
    }

    assert(!itT.hasNext()) { "First collection is bigger than second" }

    while (itR.hasNext()) {
        result.add(defaultT to itR.next())
    }

    return result
}
