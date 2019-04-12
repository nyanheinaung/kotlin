/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.*

fun decodePluginOptions(options: String): Map<String, List<String>> {
    val map = LinkedHashMap<String, List<String>>()

    val decodedBytes = Base64.getDecoder().decode(options)
    val bis = ByteArrayInputStream(decodedBytes)
    val ois = ObjectInputStream(bis)

    val n = ois.readInt()

    repeat(n) {
        val key = ois.readUTF()

        val valueCount = ois.readInt()
        val values = mutableListOf<String>()

        repeat(valueCount) {
            values += ois.readUTF()
        }

        map[key] = values
    }

    return map
}
