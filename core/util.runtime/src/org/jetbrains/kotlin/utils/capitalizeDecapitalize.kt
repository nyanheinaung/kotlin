/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.util.capitalizeDecapitalize

/**
 * "FooBar" -> "fooBar"
 * "FOOBar" -> "fooBar"
 * "FOO" -> "foo"
 * "FOO_BAR" -> "foO_BAR"
 */
fun String.decapitalizeSmartForCompiler(asciiOnly: Boolean = false): String {
    if (isEmpty() || !isUpperCaseCharAt(0, asciiOnly)) return this

    if (length == 1 || !isUpperCaseCharAt(1, asciiOnly)) {
        return if (asciiOnly) decapitalizeAsciiOnly() else decapitalize()
    }

    val secondWordStart = (indices.firstOrNull { !isUpperCaseCharAt(it, asciiOnly) } ?: return toLowerCase(this, asciiOnly)) - 1

    return toLowerCase(substring(0, secondWordStart), asciiOnly) + substring(secondWordStart)
}

/**
 * "FooBar" -> "fooBar"
 * "FOOBar" -> "fooBar"
 * "FOO" -> "foo"
 * "FOO_BAR" -> "fooBar"
 * "__F_BAR" -> "fBar"
 */
fun String.decapitalizeSmart(asciiOnly: Boolean = false): String {
    return decapitalizeWithUnderscores(this, asciiOnly)
        ?: decapitalizeSmartForCompiler(asciiOnly)
}

/**
 * "fooBar" -> "FOOBar"
 * "FooBar" -> "FOOBar"
 * "foo" -> "FOO"
 */
fun String.capitalizeFirstWord(asciiOnly: Boolean = false): String {
    val secondWordStart = indices.drop(1).firstOrNull { !isLowerCaseCharAt(it, asciiOnly) }
        ?: return toUpperCase(this, asciiOnly)

    return toUpperCase(substring(0, secondWordStart), asciiOnly) + substring(secondWordStart)
}

/**
 * FOOBAR -> null
 * FOO_BAR -> "fooBar"
 * FOO_BAR_BAZ -> "fooBarBaz"
 * "__F_BAR" -> "fBar"
 * "_F_BAR" -> "fBar"
 * "F_BAR" -> "fBar"
 */
private fun decapitalizeWithUnderscores(str: String, asciiOnly: Boolean): String? {
    val words = str.split("_").filter { it.isNotEmpty() }

    if (words.size <= 1) return null

    val builder = StringBuilder()

    words.forEachIndexed { index, word ->
        if (index == 0) {
            builder.append(toLowerCase(word, asciiOnly))
        } else {
            builder.append(toUpperCase(word.first().toString(), asciiOnly))
            builder.append(toLowerCase(word.drop(1), asciiOnly))
        }
    }

    return builder.toString()
}

private fun String.isUpperCaseCharAt(index: Int, asciiOnly: Boolean): Boolean {
    val c = this[index]
    return if (asciiOnly) c in 'A'..'Z' else c.isUpperCase()
}

private fun String.isLowerCaseCharAt(index: Int, asciiOnly: Boolean): Boolean {
    val c = this[index]
    return if (asciiOnly) c in 'a'..'z' else c.isLowerCase()
}

private fun toLowerCase(string: String, asciiOnly: Boolean): String {
    return if (asciiOnly) string.toLowerCaseAsciiOnly() else string.toLowerCase()
}

private fun toUpperCase(string: String, asciiOnly: Boolean): String {
    return if (asciiOnly) string.toUpperCaseAsciiOnly() else string.toUpperCase()
}

fun String.capitalizeAsciiOnly(): String {
    if (isEmpty()) return this
    val c = this[0]
    return if (c in 'a'..'z')
        c.toUpperCase() + substring(1)
    else
        this
}

fun String.decapitalizeAsciiOnly(): String {
    if (isEmpty()) return this
    val c = this[0]
    return if (c in 'A'..'Z')
        c.toLowerCase() + substring(1)
    else
        this
}

fun String.toLowerCaseAsciiOnly(): String {
    val builder = StringBuilder(length)
    for (c in this) {
        builder.append(if (c in 'A'..'Z') c.toLowerCase() else c)
    }
    return builder.toString()
}

fun String.toUpperCaseAsciiOnly(): String {
    val builder = StringBuilder(length)
    for (c in this) {
        builder.append(if (c in 'a'..'z') c.toUpperCase() else c)
    }
    return builder.toString()
}



