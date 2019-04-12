/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.metadata.deserialization

import org.jetbrains.kotlin.metadata.ProtoBuf

class TypeTable(typeTable: ProtoBuf.TypeTable) {
    val types: List<ProtoBuf.Type> = run {
        val originalTypes = typeTable.typeList
        if (typeTable.hasFirstNullable()) {
            val firstNullable = typeTable.firstNullable
            typeTable.typeList.mapIndexed { i, type ->
                if (i >= firstNullable) {
                    type.toBuilder().setNullable(true).build()
                } else type
            }
        } else originalTypes
    }

    operator fun get(index: Int) = types[index]
}
