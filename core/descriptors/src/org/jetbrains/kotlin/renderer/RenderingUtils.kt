/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.renderer

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.name.Name

fun Name.render(): String {
    return if (this.shouldBeEscaped()) '`' + asString() + '`' else asString()
}

private fun Name.shouldBeEscaped(): Boolean {
    if (isSpecial) return false

    val string = asString()
    return string in KeywordStringsGenerated.KEYWORDS || string.any { !Character.isLetterOrDigit(it) && it != '_' }
}

fun FqNameUnsafe.render(): String {
    return renderFqName(pathSegments())
}

fun FqName.render(): String {
    return renderFqName(pathSegments())
}

fun renderFqName(pathSegments: List<Name>): String {
    return buildString {
        for (element in pathSegments) {
            if (length > 0) {
                append(".")
            }
            append(element.render())
        }
    }
}

