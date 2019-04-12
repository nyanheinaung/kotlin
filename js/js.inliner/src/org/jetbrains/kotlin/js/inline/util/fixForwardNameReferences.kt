/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util

import org.jetbrains.kotlin.js.backend.ast.*

fun JsNode.fixForwardNameReferences() {
    accept(object : RecursiveJsVisitor() {
        val currentScope = mutableMapOf<String, JsName>()

        init {
            currentScope += collectDefinedNames(this@fixForwardNameReferences).associateBy { it.ident }
        }

        override fun visitFunction(x: JsFunction) {
            val scopeBackup = mutableMapOf<String, JsName?>()
            val localVars = x.collectLocalVariables()
            for (localVar in localVars) {
                scopeBackup[localVar.ident] = currentScope[localVar.ident]
                currentScope[localVar.ident] = localVar
            }

            super.visitFunction(x)

            for ((ident, oldName) in scopeBackup) {
                if (oldName == null) {
                    currentScope -= ident
                }
                else {
                    currentScope[ident] = oldName
                }
            }
        }

        override fun visitCatch(x: JsCatch) {
            val name = x.parameter.name
            val oldName = currentScope[name.ident]
            currentScope[name.ident] = name

            super.visitCatch(x)

            if (oldName != null) {
                currentScope[name.ident] = name
            }
            else {
                currentScope -= name.ident
            }
        }

        override fun visitNameRef(nameRef: JsNameRef) {
            super.visitNameRef(nameRef)
            if (nameRef.qualifier == null) {
                val ident = nameRef.ident
                val name = currentScope[ident]
                if (name != null) {
                    nameRef.name = name
                }
            }
        }

        override fun visitBreak(x: JsBreak) {}

        override fun visitContinue(x: JsContinue) {}
    })
}