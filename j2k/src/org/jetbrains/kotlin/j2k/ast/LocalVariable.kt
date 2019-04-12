/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

import org.jetbrains.kotlin.j2k.CodeBuilder

class LocalVariable(
        private val identifier: Identifier,
        private val annotations: Annotations,
        private val modifiers: Modifiers,
        private val explicitType: Type?,
        private val initializer: Expression,
        private val isVal: Boolean
) : Element() {

    override fun generateCode(builder: CodeBuilder) {
        if(initializer is AssignmentExpression)
            builder append initializer append "\n"
        builder append annotations append (if (isVal) "val " else "var ") append identifier
        if (explicitType != null) {
            builder append ":" append explicitType
        }
        if (!initializer.isEmpty) {
            builder append " = "
            if(initializer is AssignmentExpression)
                builder append initializer.left
            else
                builder append initializer
        }
    }
}
