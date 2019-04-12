/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.DelegatingGlobalSearchScope
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer
import javax.annotation.PostConstruct
import javax.inject.Inject

abstract class AbstractJavaClassFinder : JavaClassFinder {

    protected lateinit var project: Project
    protected lateinit var javaSearchScope: GlobalSearchScope


    @Inject
    fun setScope(scope: GlobalSearchScope) {
        javaSearchScope = FilterOutKotlinSourceFilesScope(scope)
    }

    @Inject
    open fun setProjectInstance(project: Project) {
        this.project = project
    }

    @PostConstruct
    open fun initialize(trace: BindingTrace, codeAnalyzer: KotlinCodeAnalyzer) {
        CodeAnalyzerInitializer.getInstance(project).initialize(trace, codeAnalyzer.moduleDescriptor, codeAnalyzer)
    }

    inner class FilterOutKotlinSourceFilesScope(baseScope: GlobalSearchScope) : DelegatingGlobalSearchScope(baseScope) {

        override fun contains(file: VirtualFile) = myBaseScope.contains(file) && (file.isDirectory || file.fileType !== KotlinFileType.INSTANCE)

        val base: GlobalSearchScope = myBaseScope

        //NOTE: expected by class finder to be not null
        override fun getProject(): Project = this@AbstractJavaClassFinder.project

        override fun toString() = "JCFI: $myBaseScope"

    }
}
