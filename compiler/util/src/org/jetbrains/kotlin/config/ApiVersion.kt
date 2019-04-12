/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import org.jetbrains.kotlin.utils.DescriptionAware

class ApiVersion private constructor(
        val version: MavenComparableVersion,
        val versionString: String
) : Comparable<ApiVersion>, DescriptionAware {
    val isStable: Boolean
        get() = this <= ApiVersion.LATEST_STABLE

    override val description: String
        get() = if (isStable) versionString else "$versionString (EXPERIMENTAL)"

    override fun compareTo(other: ApiVersion): Int =
            version.compareTo(other.version)

    override fun equals(other: Any?) =
            (other as? ApiVersion)?.version == version

    override fun hashCode() =
            version.hashCode()

    override fun toString() = versionString

    companion object {
        @JvmField
        val KOTLIN_1_0 = createByLanguageVersion(LanguageVersion.KOTLIN_1_0)

        @JvmField
        val KOTLIN_1_1 = createByLanguageVersion(LanguageVersion.KOTLIN_1_1)

        @JvmField
        val KOTLIN_1_2 = createByLanguageVersion(LanguageVersion.KOTLIN_1_2)

        @JvmField
        val KOTLIN_1_3 = createByLanguageVersion(LanguageVersion.KOTLIN_1_3)

        @JvmField
        val LATEST_STABLE: ApiVersion = createByLanguageVersion(LanguageVersion.LATEST_STABLE)

        @JvmStatic
        fun createByLanguageVersion(version: LanguageVersion): ApiVersion = parse(version.versionString)!!

        fun parse(versionString: String): ApiVersion? = try {
            ApiVersion(MavenComparableVersion(versionString), versionString)
        }
        catch (e: Exception) {
            null
        }
    }
}
