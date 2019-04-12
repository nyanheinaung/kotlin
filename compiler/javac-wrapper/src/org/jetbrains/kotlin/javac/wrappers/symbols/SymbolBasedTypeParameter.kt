/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaClassifierType
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.TypeParameterElement

class SymbolBasedTypeParameter(
        element: TypeParameterElement,
        javac: JavacWrapper
) : SymbolBasedClassifier<TypeParameterElement>(element, javac), JavaTypeParameter {

    override val name: Name
        get() = Name.identifier(element.simpleName.toString())

    override val upperBounds: Collection<JavaClassifierType>
        get() = element.bounds.map { SymbolBasedClassifierType(it, javac) }

}