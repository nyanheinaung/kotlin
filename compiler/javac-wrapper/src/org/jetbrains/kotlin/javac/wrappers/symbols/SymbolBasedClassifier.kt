/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationOwner
import org.jetbrains.kotlin.load.java.structure.JavaClassifier
import org.jetbrains.kotlin.name.FqName
import javax.lang.model.element.Element

abstract class SymbolBasedClassifier<out T : Element>(
        element: T,
        javac: JavacWrapper
) : SymbolBasedElement<T>(element, javac), JavaClassifier, JavaAnnotationOwner {

    override val annotations: Collection<JavaAnnotation>
        get() = element.annotationMirrors.map { SymbolBasedAnnotation(it, javac) }

    override fun findAnnotation(fqName: FqName) = element.findAnnotation(fqName, javac)

    override val isDeprecatedInJavaDoc: Boolean
        get() = javac.isDeprecated(element)

}
