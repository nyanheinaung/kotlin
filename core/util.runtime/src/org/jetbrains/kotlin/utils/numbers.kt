/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

data class NumberWithRadix(val number: String, val radix: Int)

fun extractRadix(value: String): NumberWithRadix = when {
    value.startsWith("0x") || value.startsWith("0X") -> NumberWithRadix(value.substring(2), 16)
    value.startsWith("0b") || value.startsWith("0B") -> NumberWithRadix(value.substring(2), 2)
    else -> NumberWithRadix(value, 10)
}
