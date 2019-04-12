/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import java.io.*

data class BuildInfo(val startTS: Long) : Serializable {
    companion object {
        fun read(file: File): BuildInfo? =
                try {
                    ObjectInputStream(FileInputStream(file)).use {
                        it.readObject() as BuildInfo
                    }
                }
                catch (e: Exception) {
                    null
                }

        fun write(buildInfo: BuildInfo, file: File) {
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeObject(buildInfo)
            }
        }
    }
}