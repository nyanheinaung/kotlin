/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.name

import java.util.*

object NameUtils {
    private val SANITIZE_AS_JAVA_INVALID_CHARACTERS = "[^\\p{L}\\p{Digit}]".toRegex()

    @JvmStatic
    fun sanitizeAsJavaIdentifier(name: String): String {
        return SANITIZE_AS_JAVA_INVALID_CHARACTERS.replace(name, "_")
    }

    /**
     * Capitalizes the short name of the file (without extension) and sanitizes it so that it's a valid Java identifier.
     * E.g. "fileName" -> "FileName", "1" -> "_1", "" -> "_"
     */
    @JvmStatic
    fun getPackagePartClassNamePrefix(shortFileName: String): String =
            if (shortFileName.isEmpty())
                "_"
            else
                capitalizeAsJavaClassName(sanitizeAsJavaIdentifier(shortFileName))

    @JvmStatic
    private fun capitalizeAsJavaClassName(str: String): String =
            // NB use Locale.ENGLISH so that build is locale-independent.
            // See Javadoc on java.lang.String.toUpperCase() for more details.
            if (Character.isJavaIdentifierStart(str[0]))
                str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1)
            else
                "_$str"

    // "pkg/someScript.kts" -> "SomeScript"
    @JvmStatic
    fun getScriptNameForFile(filePath: String): Name =
            Name.identifier(NameUtils.getPackagePartClassNamePrefix(filePath.substringAfterLast('/').substringBeforeLast('.')))

    @JvmStatic
    fun hasName(name: Name) = name != SpecialNames.NO_NAME_PROVIDED && name != SpecialNames.ANONYMOUS_FUNCTION
}
