/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.load.java.structure.JavaType
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement

class SymbolBasedField(
        element: VariableElement,
        containingClass: JavaClass,
        javac: JavacWrapper
) : SymbolBasedMember<VariableElement>(element, containingClass, javac), JavaField {

    override val isEnumEntry: Boolean
        get() = element.kind == ElementKind.ENUM_CONSTANT

    override val type: JavaType
        get() = SymbolBasedType.create(element.asType(), javac)

    override val initializerValue: Any?
        by lazy { element.constantValue }

    override val hasConstantNotNullInitializer: Boolean
        get() = initializerValue != null

}