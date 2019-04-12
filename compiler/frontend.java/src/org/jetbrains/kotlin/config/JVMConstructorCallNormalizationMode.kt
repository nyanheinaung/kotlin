/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

enum class JVMConstructorCallNormalizationMode(
    val description: String,
    val isEnabled: Boolean,
    val shouldPreserveClassInitialization: Boolean
) {
    DISABLE("disable", false, false),
    ENABLE("enable", true, false),
    PRESERVE_CLASS_INITIALIZATION("preserve-class-initialization", true, true)
    ;

    companion object {
        @JvmStatic
        fun isSupportedValue(string: String?) =
            string == null || values().any { it.description == string }

        @JvmStatic
        fun fromStringOrNull(string: String?) =
            values().find { it.description == string }
    }
}