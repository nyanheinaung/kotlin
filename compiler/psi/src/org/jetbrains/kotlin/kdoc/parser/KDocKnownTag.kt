/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.parser

import com.intellij.openapi.util.text.StringUtil

enum class KDocKnownTag(val isReferenceRequired: Boolean, val isSectionStart: Boolean) {
    AUTHOR(false, false),
    THROWS(true, false),
    EXCEPTION(true, false),
    PARAM(true, false),
    RECEIVER(false, false),
    RETURN(false, false),
    SEE(true, false),
    SINCE(false, false),
    CONSTRUCTOR(false, true),
    PROPERTY(true, true),
    SAMPLE(true, false),
    SUPPRESS(false, false);


    companion object {
        fun findByTagName(tagName: CharSequence): KDocKnownTag? {
            var tagName = tagName
            if (StringUtil.startsWith(tagName, "@")) {
                tagName = tagName.subSequence(1, tagName.length)
            }
            try {
                return valueOf(tagName.toString().toUpperCase())
            } catch (ignored: IllegalArgumentException) {
            }

            return null
        }
    }
}
