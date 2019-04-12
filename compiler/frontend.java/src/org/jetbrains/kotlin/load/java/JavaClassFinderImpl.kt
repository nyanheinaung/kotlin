/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.impl.JavaPackageImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.jvm.KotlinJavaPsiFacade
import javax.inject.Inject

class JavaClassFinderImpl : AbstractJavaClassFinder() {

    private lateinit var javaFacade: KotlinJavaPsiFacade

    @Inject
    override fun setProjectInstance(project: Project) {
        super.setProjectInstance(project)
        javaFacade = KotlinJavaPsiFacade.getInstance(project)
    }

    override fun findClass(request: JavaClassFinder.Request): JavaClass? = javaFacade.findClass(request, javaSearchScope)

    override fun findPackage(fqName: FqName) =
        javaFacade.findPackage(fqName.asString(), javaSearchScope)?.let { JavaPackageImpl(it, javaSearchScope) }

    override fun knownClassNamesInPackage(packageFqName: FqName): Set<String>? = javaFacade.knownClassNamesInPackage(packageFqName)

}
