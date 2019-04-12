/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics

import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticRenderer

class RenderedDiagnostic<D : Diagnostic>(
    val diagnostic: D,
    val renderer: DiagnosticRenderer<D>
) {
    val text = renderer.render(diagnostic)

    val factory: DiagnosticFactory<*> get() = diagnostic.factory

    override fun toString() = text
}
