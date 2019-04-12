/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast

import java.io.Reader

data class JsLocation(
        override val file: String,
        override val startLine: Int,
        override val startChar: Int
) : JsLocationWithSource {
    override val identityObject: Any? = null
    override val sourceProvider: () -> Reader? = { null }

    override fun asSimpleLocation(): JsLocation = this
}

interface JsLocationWithSource {
    val file: String
    val startLine: Int
    val startChar: Int
    val identityObject: Any?
    val sourceProvider: () -> Reader?

    fun asSimpleLocation(): JsLocation
}

class JsLocationWithEmbeddedSource(
        private val location: JsLocation,
        override val identityObject: Any?,
        override val sourceProvider: () -> Reader?
) : JsLocationWithSource by location