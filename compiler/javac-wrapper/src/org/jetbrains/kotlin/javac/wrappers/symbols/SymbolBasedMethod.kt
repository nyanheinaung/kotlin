/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.ExecutableElement

class SymbolBasedMethod(
        element: ExecutableElement,
        containingClass: JavaClass,
        javac: JavacWrapper
) : SymbolBasedMember<ExecutableElement>(element, containingClass, javac), JavaMethod {

    override val typeParameters: List<JavaTypeParameter>
        get() = element.typeParameters.map { SymbolBasedTypeParameter(it, javac) }

    override val valueParameters: List<JavaValueParameter>
        get() = element.valueParameters(javac)

    override val returnType: JavaType
        get() = SymbolBasedType.create(element.returnType, javac)

    // TODO: allow nullable names in Symbol-based annotation arguments and pass null instead of a synthetic name
    override val annotationParameterDefaultValue: JavaAnnotationArgument?
        get() = element.defaultValue?.let { defaultValue ->
            SymbolBasedAnnotationArgument.create(defaultValue, Name.identifier("value"), javac)
        }
}
