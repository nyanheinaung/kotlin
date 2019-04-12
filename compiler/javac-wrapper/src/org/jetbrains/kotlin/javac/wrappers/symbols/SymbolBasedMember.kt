/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaMember
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.Element

abstract class SymbolBasedMember<out T : Element>(
        element: T,
        override val containingClass: JavaClass,
        javac: JavacWrapper
) : SymbolBasedElement<T>(element, javac), JavaMember {

    override val annotations: Collection<JavaAnnotation>
        get() = element.annotationMirrors.map { SymbolBasedAnnotation(it, javac) }

    override fun findAnnotation(fqName: FqName) = element.findAnnotation(fqName, javac)

    override val visibility: Visibility
        get() = element.getVisibility()

    override val name: Name
        get() = Name.identifier(element.simpleName.toString())

    override val isDeprecatedInJavaDoc: Boolean
        get() = javac.isDeprecated(element)

    override val isAbstract: Boolean
        get() = element.isAbstract

    override val isStatic: Boolean
        get() = element.isStatic

    override val isFinal: Boolean
        get() = element.isFinal

}