/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils.intellij

import com.intellij.openapi.util.Pair

fun <A> Pair<A, *>.component1(): A = getFirst()

fun <B> Pair<*, B>.component2(): B = getSecond()
