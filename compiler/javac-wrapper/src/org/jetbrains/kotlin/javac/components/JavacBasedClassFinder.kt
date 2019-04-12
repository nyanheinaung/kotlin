/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.components

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.AbstractJavaClassFinder
import org.jetbrains.kotlin.load.java.JavaClassFinder
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer

class JavacBasedClassFinder : AbstractJavaClassFinder() {

    private lateinit var javac: JavacWrapper

    override fun initialize(trace: BindingTrace, codeAnalyzer: KotlinCodeAnalyzer) {
        javac = JavacWrapper.getInstance(project)
        super.initialize(trace, codeAnalyzer)
    }

    override fun findClass(request: JavaClassFinder.Request) =
        // TODO: reuse previouslyFoundClassFileContent if it's possible in javac
        javac.findClass(request.classId, javaSearchScope)

    override fun findPackage(fqName: FqName) = javac.findPackage(fqName, javaSearchScope)

    override fun knownClassNamesInPackage(packageFqName: FqName) = javac.knownClassNamesInPackage(packageFqName)

}
