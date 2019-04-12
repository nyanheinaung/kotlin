/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental.snapshots

import java.io.File
import java.security.MessageDigest

internal val File.md5: ByteArray
    get() {
        val messageDigest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(4048)
        inputStream().use { input ->
            while (true) {
                val len = input.read(buffer)
                if (len < 0) {
                    break
                }
                messageDigest.update(buffer, 0, len)
            }
        }
        return messageDigest.digest()
    }
