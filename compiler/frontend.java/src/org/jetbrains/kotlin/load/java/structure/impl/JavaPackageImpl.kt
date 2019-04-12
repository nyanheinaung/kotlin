/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl

import com.intellij.psi.PsiPackage
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class JavaPackageImpl(
        psiPackage: PsiPackage, private val scope: GlobalSearchScope
) : JavaElementImpl<PsiPackage>(psiPackage), JavaPackage, MapBasedJavaAnnotationOwner {

    override fun getClasses(nameFilter: (Name) -> Boolean): Collection<JavaClass> {
        val psiClasses = psi.getClasses(scope).filter {
            val name = it.name
            name != null && nameFilter(Name.identifier(name))
        }
        return classes(psiClasses)
    }

    override val subPackages: Collection<JavaPackage>
        get() = packages(psi.getSubPackages(scope), scope)

    override val fqName: FqName
        get() = FqName(psi.qualifiedName)

    override val annotations: Collection<JavaAnnotation>
        get() = org.jetbrains.kotlin.load.java.structure.impl.annotations(psi.annotationList?.annotations.orEmpty())

    override val annotationsByFqName: Map<FqName?, JavaAnnotation> by buildLazyValueForMap()
}
