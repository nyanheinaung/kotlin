/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer

interface CodeAnalyzerInitializer {
    fun initialize(trace: BindingTrace, module: ModuleDescriptor, codeAnalyzer: KotlinCodeAnalyzer)
    fun createTrace(): BindingTrace

    companion object {
        fun getInstance(project: Project): CodeAnalyzerInitializer =
            ServiceManager.getService<CodeAnalyzerInitializer>(project, CodeAnalyzerInitializer::class.java)!!
    }
}

class DummyCodeAnalyzerInitializer : CodeAnalyzerInitializer {
    override fun initialize(trace: BindingTrace, module: ModuleDescriptor, codeAnalyzer: KotlinCodeAnalyzer) {
        // Do nothing
    }

    override fun createTrace(): BindingTrace = BindingTraceContext(true)
}
