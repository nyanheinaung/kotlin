/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

enum class ReportLevel(val description: String) {
    IGNORE("ignore"),
    WARN("warn"),
    STRICT("strict"),
    ;

    companion object {
        fun findByDescription(description: String?): ReportLevel? = values().firstOrNull { it.description == description }
    }

    val isWarning: Boolean get() = this == ReportLevel.WARN
    val isIgnore: Boolean get() = this == ReportLevel.IGNORE
}

data class Jsr305State(
        val global: ReportLevel,
        val migration: ReportLevel?,
        val user: Map<String, ReportLevel>,
        val enableCompatqualCheckerFrameworkAnnotations: Boolean = COMPATQUAL_CHECKER_FRAMEWORK_ANNOTATIONS_SUPPORT_DEFAULT_VALUE
) {
    val description: Array<String> by lazy {
        val result = mutableListOf<String>()
        result.add(global.description)

        migration?.let { result.add("under-migration:${it.description}") }

        user.forEach {
            result.add("@${it.key}:${it.value.description}")
        }

        result.toTypedArray()
    }

    val disabled: Boolean get() = this === DISABLED

    companion object {
        const val COMPATQUAL_CHECKER_FRAMEWORK_ANNOTATIONS_SUPPORT_DEFAULT_VALUE = true

        @JvmField
        val DEFAULT: Jsr305State = Jsr305State(ReportLevel.WARN, null, emptyMap())

        @JvmField
        val DISABLED: Jsr305State = Jsr305State(ReportLevel.IGNORE, ReportLevel.IGNORE, emptyMap())

        @JvmField
        val STRICT: Jsr305State = Jsr305State(ReportLevel.STRICT, ReportLevel.STRICT, emptyMap())
    }
}
