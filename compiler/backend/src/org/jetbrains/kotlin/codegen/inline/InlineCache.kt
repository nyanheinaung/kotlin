/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import com.intellij.util.containers.SLRUMap
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.org.objectweb.asm.commons.Method

data class MethodId(val ownerInternalName: String, val method: Method)

class InlineCache {
    val classBytes: SLRUMap<ClassId, ByteArray> = SLRUMap(30, 20)
    val methodNodeById: SLRUMap<MethodId, SMAPAndMethodNode> = SLRUMap(60, 50)
}

inline fun <K, V> SLRUMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        put(key, answer)
        answer
    } else {
        value
    }
}
