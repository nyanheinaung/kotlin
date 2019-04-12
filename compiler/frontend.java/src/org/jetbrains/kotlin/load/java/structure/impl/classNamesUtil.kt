/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl

import org.jetbrains.kotlin.name.SpecialNames

fun String.convertCanonicalNameToQName() = splitCanonicalFqName().joinToString(separator = ".") { it.substringBefore('<') }

// "test.A<B.C>.D<E<F.G, H>, I.J>" -> ["test", "A<B.C>", "D<E<F.G, H>, I.J>"]
private fun String.splitCanonicalFqName(): List<String> {
    fun String.toNonEmpty(): String =
            if (this.isNotEmpty()) this else SpecialNames.SAFE_IDENTIFIER_FOR_NO_NAME.asString()

    val result = arrayListOf<String>()
    var balance = 0
    var currentNameStart = 0
    for ((index, character) in this.withIndex()) {
        when (character) {
            '.' -> if (balance == 0) {
                result.add(this.substring(currentNameStart, index).toNonEmpty())
                currentNameStart = index + 1
            }
            '<' -> balance++
            '>' -> balance--
        }
    }
    result.add(this.substring(currentNameStart).toNonEmpty())
    return result
}
