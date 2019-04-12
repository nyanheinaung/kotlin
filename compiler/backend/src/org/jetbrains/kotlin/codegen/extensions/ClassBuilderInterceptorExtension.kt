/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.extensions

import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.resolve.BindingContext

interface ClassBuilderInterceptorExtension {
    companion object : ProjectExtensionDescriptor<ClassBuilderInterceptorExtension>(
            "org.jetbrains.kotlin.classBuilderFactoryInterceptorExtension", ClassBuilderInterceptorExtension::class.java)

    fun interceptClassBuilderFactory(
            interceptedFactory: ClassBuilderFactory,
            bindingContext: BindingContext,
            diagnostics: DiagnosticSink
    ): ClassBuilderFactory

}