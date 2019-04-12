/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import org.gradle.util.GradleVersion

internal data class ParsedGradleVersion(val major: Int, val minor: Int) : Comparable<ParsedGradleVersion> {
    override fun compareTo(other: ParsedGradleVersion): Int {
        val majorCompare = major.compareTo(other.major)
        if (majorCompare != 0) return majorCompare

        return minor.compareTo(other.minor)
    }

    companion object {
        private fun String.parseIntOrNull(): Int? =
            try {
                toInt()
            } catch (e: NumberFormatException) {
                null
            }

        fun parse(version: String): ParsedGradleVersion? {
            val matches = "(\\d+)\\.(\\d+).*"
                .toRegex()
                .find(version)
                ?.groups
                ?.drop(1)?.take(2)
                // checking if two subexpression groups are found and length of each is >0 and <4
                ?.let { if (it.all { (it?.value?.length ?: 0).let { it > 0 && it < 4 } }) it else null }

            val versions = matches?.mapNotNull { it?.value?.parseIntOrNull() } ?: emptyList()
            if (versions.size == 2 && versions.all { it >= 0 }) {
                val (major, minor) = versions
                return ParsedGradleVersion(major, minor)
            }

            return null
        }
    }
}

fun isGradleVersionAtLeast(major: Int, minor: Int) =
    ParsedGradleVersion.parse(GradleVersion.current().version)
        ?.let { it >= ParsedGradleVersion(major, minor) } ?: false