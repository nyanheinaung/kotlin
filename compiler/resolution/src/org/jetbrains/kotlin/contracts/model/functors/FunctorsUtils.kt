/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.functors

/**
 * Applies [operation] to [first] and [second] if both not-null, otherwise returns null
 */
internal fun <F, S, R> applyIfBothNotNull(first: F?, second: S?, operation: (F, S) -> R): R? =
    if (first == null || second == null) null else operation(first, second)

/**
 * If both [first] and [second] are null, then return null
 * If only one of [first] and [second] is null, then return other one
 * Otherwise, return result of [operation]
 */
internal fun <F : R, S : R, R> applyWithDefault(first: F?, second: S?, operation: (F, S) -> R): R? = when {
    first == null && second == null -> null
    first == null -> second
    second == null -> first
    else -> operation(first, second)
}
