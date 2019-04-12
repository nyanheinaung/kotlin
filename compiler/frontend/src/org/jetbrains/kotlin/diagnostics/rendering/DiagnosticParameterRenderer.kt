/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering

interface DiagnosticParameterRenderer<in O> {
    fun render(obj: O, renderingContext: RenderingContext): String
}

fun <O> Renderer(block: (O) -> String) = object : DiagnosticParameterRenderer<O> {
    override fun render(obj: O, renderingContext: RenderingContext): String = block(obj)
}

fun <O> ContextDependentRenderer(block: (O, RenderingContext) -> String) = object : DiagnosticParameterRenderer<O> {
    override fun render(obj: O, renderingContext: RenderingContext): String = block(obj, renderingContext)
}