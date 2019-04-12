/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.util

fun String.addBeforeSubstring(prefix: String, substring: String): String =
    replace(substring, prefix + substring)

fun String.checkedReplace(original: String, replacement: String): String {
    check(contains(original)) { "Substring '$original' is not found in '$this'" }
    return replace(original, replacement)
}