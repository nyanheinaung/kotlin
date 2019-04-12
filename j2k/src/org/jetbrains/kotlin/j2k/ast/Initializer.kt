/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

import org.jetbrains.kotlin.j2k.CodeBuilder

class Initializer(val body: DeferredElement<Block>, modifiers: Modifiers) : Member(Annotations.Empty, modifiers) {
    override fun generateCode(builder: CodeBuilder) {
        builder append "init" append body
    }

    override val isEmpty: Boolean
        get() = body.isEmpty

    // need to override it to not use isEmpty
    override val canBeSingleton: Boolean
        get() = false
}
