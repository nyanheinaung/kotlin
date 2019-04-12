/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

import org.jetbrains.kotlin.j2k.CodeBuilder
import org.jetbrains.kotlin.j2k.append

class ReferenceElement(val name: Identifier, val typeArgs: List<Element>) : Element() {
    override fun generateCode(builder: CodeBuilder) {
        builder.append(name).append(typeArgs, ", ", "<", ">")
    }
}
