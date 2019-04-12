/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.util.io.StringRef

object Utils {
    fun wrapStrings(names: List<String>): Array<StringRef> {
        return Array(names.size) { i -> StringRef.fromString(names[i])!! }
    }
}
