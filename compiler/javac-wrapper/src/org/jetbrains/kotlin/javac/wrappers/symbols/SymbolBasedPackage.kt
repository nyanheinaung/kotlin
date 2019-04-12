/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaPackage
import org.jetbrains.kotlin.load.java.structure.MapBasedJavaAnnotationOwner
import org.jetbrains.kotlin.load.java.structure.buildLazyValueForMap
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.PackageElement

class SymbolBasedPackage(
        element: PackageElement,
        javac: JavacWrapper
) : SymbolBasedElement<PackageElement>(element, javac), JavaPackage, MapBasedJavaAnnotationOwner {

    override val fqName: FqName
        get() = FqName(element.qualifiedName.toString())

    override val subPackages: Collection<JavaPackage>
        get() = javac.findSubPackages(fqName)


    override val annotations: Collection<JavaAnnotation>
        get() = element.annotationMirrors.map { SymbolBasedAnnotation(it, javac) }

    override val annotationsByFqName: Map<FqName?, JavaAnnotation> by buildLazyValueForMap()

    override fun getClasses(nameFilter: (Name) -> Boolean) =
            javac.findClassesFromPackage(fqName).filter { nameFilter(it.name) }

    override fun toString() = element.qualifiedName.toString()

}
