/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaType
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.VariableElement

class SymbolBasedValueParameter(
        element: VariableElement,
        private val elementName : String,
        override val isVararg : Boolean,
        javac: JavacWrapper
) : SymbolBasedElement<VariableElement>(element, javac), JavaValueParameter {

    override val annotations: Collection<JavaAnnotation>
        get() = element.annotationMirrors.map { SymbolBasedAnnotation(it, javac) }

    override fun findAnnotation(fqName: FqName) =
            element.findAnnotation(fqName, javac)

    override val isDeprecatedInJavaDoc: Boolean
        get() =  javac.isDeprecated(element)

    override val name: Name
        get() = Name.identifier(elementName)

    override val type: JavaType
        get() = SymbolBasedType.create(element.asType(), javac)

}