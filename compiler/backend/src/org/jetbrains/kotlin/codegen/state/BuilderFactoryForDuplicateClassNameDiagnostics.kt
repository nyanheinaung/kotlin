/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.state

import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.ClassNameCollectionClassBuilderFactory
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin


class BuilderFactoryForDuplicateClassNameDiagnostics(
        builderFactory: ClassBuilderFactory,
        private val diagnostics: DiagnosticSink
) : ClassNameCollectionClassBuilderFactory(builderFactory) {

    private val className = hashMapOf<String, JvmDeclarationOrigin> ()

    override fun handleClashingNames(internalName: String, origin: JvmDeclarationOrigin) {
        val another = className.getOrPut(internalName, { origin })
        //workaround for inlined anonymous objects
        if (origin.element != another.element) {
            reportError(internalName, origin, another)
        }
    }

    private fun reportError(internalName: String, vararg another: JvmDeclarationOrigin) {
        val fromString = another.mapNotNull { it.descriptor }.
                joinToString { DescriptorRenderer.ONLY_NAMES_WITH_SHORT_TYPES.render(it) }

        another.mapNotNull { it.element }.forEach {
            diagnostics.report(ErrorsJvm.DUPLICATE_CLASS_NAMES.on(it, internalName, fromString))
        }
    }
}