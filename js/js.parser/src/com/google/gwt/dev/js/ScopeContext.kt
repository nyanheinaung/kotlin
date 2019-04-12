/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.google.gwt.dev.js

import org.jetbrains.kotlin.js.backend.ast.*
import java.util.*

class ScopeContext(scope: JsScope) {
    private val rootScope = generateSequence(scope) { it.parent }.first { it is JsRootScope }
    private val scopes = Stack<JsScope>()

    init {
        scopes.push(scope)
    }

    fun enterFunction(): JsFunction {
        val fn = JsFunction(currentScope, "<js function>")
        enterScope(fn.scope)
        return fn
    }

    fun exitFunction() {
        assert(currentScope is JsDeclarationScope)
        exitScope()
    }

    fun enterCatch(ident: String): JsCatch {
        val jsCatch = JsCatch(currentScope, ident)
        enterScope(jsCatch.scope)
        return jsCatch
    }

    fun exitCatch() {
        assert(currentScope is JsCatchScope)
        exitScope()
    }

    fun enterLabel(ident: String, outputName: String): JsName =
            (currentScope as JsDeclarationScope).enterLabel(ident, outputName)

    fun exitLabel() =
            (currentScope as JsDeclarationScope).exitLabel()

    fun labelFor(ident: String): JsName? =
            (currentScope as JsDeclarationScope).findLabel(ident)

    fun globalNameFor(ident: String): JsName =
            currentScope.findName(ident) ?: rootScope.declareName(ident)

    fun localNameFor(ident: String): JsName =
            currentScope.findOwnNameOrDeclare(ident)

    fun referenceFor(ident: String): JsNameRef =
            JsNameRef(ident)

    private fun enterScope(scope: JsScope) = scopes.push(scope)

    private fun exitScope() = scopes.pop()

    private val currentScope: JsScope
        get() = scopes.peek()
}

/**
 * Overrides JsFunctionScope declareName as it's mapped to declareFreshName
 */
private fun JsScope.findOwnNameOrDeclare(ident: String): JsName =
        when (this) {
            is JsFunctionScope -> declareNameUnsafe(ident)
            else -> declareName(ident)
        }