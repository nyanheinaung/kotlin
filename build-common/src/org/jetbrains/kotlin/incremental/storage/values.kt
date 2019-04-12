/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.storage

data class LookupSymbolKey(val nameHash: Int, val scopeHash: Int) : Comparable<LookupSymbolKey> {
    constructor(name: String, scope: String) : this(name.hashCode(), scope.hashCode())

    override fun compareTo(other: LookupSymbolKey): Int {
        val nameCmp = nameHash.compareTo(other.nameHash)

        if (nameCmp != 0) return nameCmp

        return scopeHash.compareTo(other.scopeHash)
    }
}

data class ProtoMapValue(val isPackageFacade: Boolean, val bytes: ByteArray, val strings: Array<String>)
