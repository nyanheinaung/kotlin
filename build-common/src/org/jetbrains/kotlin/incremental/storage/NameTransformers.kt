/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.jvm.JvmClassName

internal interface NameTransformer<Name> {
    fun asString(name: Name): String
    fun asName(string: String): Name
    fun asFqName(string: String): FqName
}

internal object FqNameTransformer : NameTransformer<FqName> {
    override fun asString(name: FqName): String =
        name.asString()

    override fun asName(string: String): FqName =
        FqName(string)

    override fun asFqName(string: String): FqName =
        asName(string)
}

internal object JvmClassNameTransformer : NameTransformer<JvmClassName> {
    override fun asString(name: JvmClassName): String =
        name.internalName

    override fun asName(string: String): JvmClassName =
        JvmClassName.byInternalName(string)

    override fun asFqName(string: String): FqName =
        asName(string).fqNameForClassNameWithoutDollars
}