/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic

import org.jetbrains.kotlin.android.synthetic.res.ResourceIdentifier

object AndroidConst {
    val SYNTHETIC_PACKAGE: String = "kotlinx.android.synthetic"
    val SYNTHETIC_PACKAGE_PATH_LENGTH = SYNTHETIC_PACKAGE.count { it == '.' } + 1

    val SYNTHETIC_SUBPACKAGES: List<String> = SYNTHETIC_PACKAGE.split('.').fold(arrayListOf<String>()) { list, segment ->
        val prevSegment = list.lastOrNull()?.let { "$it." } ?: ""
        list += "$prevSegment$segment"
        list
    }

    val ANDROID_NAMESPACE: String = "http://schemas.android.com/apk/res/android"
    val ID_ATTRIBUTE_NO_NAMESPACE: String = "id"
    val CLASS_ATTRIBUTE_NO_NAMESPACE: String = "class"

    private val IDENTIFIER_WORD_REGEX = "[(?:\\p{L}\\p{M}*)0-9_\\.\\:\\-]+"
    val IDENTIFIER_REGEX = "^@(\\+)?(($IDENTIFIER_WORD_REGEX)\\:)?id\\/($IDENTIFIER_WORD_REGEX)$".toRegex()

    val CLEAR_FUNCTION_NAME = "clearFindViewByIdCache"


    //TODO FqName / ClassId

    val VIEW_FQNAME = "android.view.View"
    val VIEWSTUB_FQNAME = "android.view.ViewStub"

    val ACTIVITY_FQNAME = "android.app.Activity"
    val FRAGMENT_FQNAME = "android.app.Fragment"
    val DIALOG_FQNAME = "android.app.Dialog"
    val SUPPORT_V4_PACKAGE = "android.support.v4"
    val SUPPORT_FRAGMENT_FQNAME = "$SUPPORT_V4_PACKAGE.app.Fragment"
    val SUPPORT_FRAGMENT_ACTIVITY_FQNAME = "$SUPPORT_V4_PACKAGE.app.FragmentActivity"
    val ANDROIDX_SUPPORT_FRAGMENT_FQNAME = "androidx.fragment.app.Fragment"
    val ANDROIDX_SUPPORT_FRAGMENT_ACTIVITY_FQNAME = "androidx.fragment.app.FragmentActivity"

    val IGNORED_XML_WIDGET_TYPES = setOf("requestFocus", "merge", "tag", "check", "blink")

    val FQNAME_RESOLVE_PACKAGES = listOf("android.widget", "android.webkit", "android.view")
}

fun androidIdToName(id: String): ResourceIdentifier? {
    val values = AndroidConst.IDENTIFIER_REGEX.matchEntire(id)?.groupValues ?: return null
    val packageName = values[3]

    return ResourceIdentifier(
        getJavaIdentifierNameForResourceName(values[4]),
        if (packageName.isEmpty()) null else packageName
    )
}

// See also AndroidResourceUtil#getFieldNameByResourceName()
fun getJavaIdentifierNameForResourceName(styleName: String) = buildString {
    for (char in styleName) {
        when (char) {
            '.', '-', ':' -> append('_')
            else -> append(char)
        }
    }
}

fun isWidgetTypeIgnored(xmlType: String): Boolean {
    return (xmlType.isEmpty() || xmlType in AndroidConst.IGNORED_XML_WIDGET_TYPES)
}

internal fun <T> List<T>.forEachUntilLast(operation: (T) -> Unit) {
    val lastIndex = lastIndex
    forEachIndexed { i, t ->
        if (i < lastIndex) {
            operation(t)
        }
    }
}
