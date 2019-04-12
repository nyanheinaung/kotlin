/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

import org.jetbrains.kotlin.j2k.CodeBuilder

abstract class Expression : Statement() {
    open val isNullable: Boolean get() = false

    object Empty : Expression() {
        override fun generateCode(builder: CodeBuilder) {}
        override val isEmpty: Boolean get() = true
    }
}
