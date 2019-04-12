/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.storage.LockBasedStorageManager

class DefaultBuiltIns(loadBuiltInsFromCurrentClassLoader: Boolean = true) : KotlinBuiltIns(LockBasedStorageManager("DefaultBuiltIns")) {
    init {
        if (loadBuiltInsFromCurrentClassLoader) {
            createBuiltInsModule(false)
        }
    }

    companion object {
        @JvmStatic
        val Instance: DefaultBuiltIns =
            DefaultBuiltIns()
    }
}
