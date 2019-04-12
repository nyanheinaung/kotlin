/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

private val IDENTITY: (Any?) -> Any? = { it }

@Suppress("UNCHECKED_CAST") fun <T> identity(): (T) -> T = IDENTITY as (T) -> T


private val ALWAYS_TRUE: (Any?) -> Boolean = { true }

fun <T> alwaysTrue(): (T) -> Boolean = ALWAYS_TRUE

private val ALWAYS_NULL: (Any?) -> Any? = { null }

@Suppress("UNCHECKED_CAST")
fun <T, R: Any> alwaysNull(): (T) -> R? = ALWAYS_NULL as (T) -> R?

val DO_NOTHING: (Any?) -> Unit = { }
val DO_NOTHING_2: (Any?, Any?) -> Unit = { _, _ -> }
val DO_NOTHING_3: (Any?, Any?, Any?) -> Unit = { _, _, _ -> }

fun <T> doNothing(): (T) -> Unit = DO_NOTHING

fun doNothing() {}