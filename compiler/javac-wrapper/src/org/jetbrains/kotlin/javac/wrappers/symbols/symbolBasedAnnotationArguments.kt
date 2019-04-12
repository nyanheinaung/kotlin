/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import com.sun.tools.javac.code.Symbol
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

sealed class SymbolBasedAnnotationArgument(
        override val name: Name,
        protected val javac: JavacWrapper
) : JavaAnnotationArgument, JavaElement {

    companion object {
        fun create(value: Any, name: Name, javac: JavacWrapper): JavaAnnotationArgument = when (value) {
            is AnnotationMirror -> SymbolBasedAnnotationAsAnnotationArgument(value, name, javac)
            is VariableElement -> SymbolBasedReferenceAnnotationArgument(value, name, javac)
            is TypeMirror -> SymbolBasedClassObjectAnnotationArgument(value, name, javac)
            is Collection<*> -> arrayAnnotationArguments(value, name, javac)
            is AnnotationValue -> create(value.value, name, javac)
            else -> SymbolBasedLiteralAnnotationArgument(value, name, javac)
        }

        private fun arrayAnnotationArguments(values: Collection<*>, name: Name, javac: JavacWrapper): JavaArrayAnnotationArgument =
                values.map { create(it!!, name, javac) }
                .let { argumentList -> SymbolBasedArrayAnnotationArgument(argumentList, name, javac) }

    }

}

class SymbolBasedAnnotationAsAnnotationArgument(
        private val mirror: AnnotationMirror,
        name: Name,
        javac: JavacWrapper
) : SymbolBasedAnnotationArgument(name, javac), JavaAnnotationAsAnnotationArgument {

    override fun getAnnotation() = SymbolBasedAnnotation(mirror, javac)

}

class SymbolBasedReferenceAnnotationArgument(
        val element: VariableElement,
        name: Name,
        javac: JavacWrapper
) : SymbolBasedAnnotationArgument(name, javac), JavaEnumValueAnnotationArgument {
    // TODO: do not create extra objects here
    private val javaField: JavaField? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val containingClass = element.enclosingElement as Symbol.ClassSymbol
        SymbolBasedField(element, SymbolBasedClass(containingClass, javac, null, containingClass.classfile), javac)
    }

    override val enumClassId: ClassId?
        get() = javaField?.containingClass?.classId

    override val entryName: Name?
        get() = javaField?.name
}

class SymbolBasedClassObjectAnnotationArgument(
        private val type: TypeMirror,
        name : Name,
        javac: JavacWrapper
) : SymbolBasedAnnotationArgument(name, javac), JavaClassObjectAnnotationArgument {

    override fun getReferencedType() = SymbolBasedType.create(type, javac)

}

class SymbolBasedArrayAnnotationArgument(
        val args : List<JavaAnnotationArgument>,
        name : Name,
        javac: JavacWrapper
) : SymbolBasedAnnotationArgument(name, javac), JavaArrayAnnotationArgument {

    override fun getElements() = args

}

class SymbolBasedLiteralAnnotationArgument(
        override val value : Any,
        name : Name,
        javac: JavacWrapper
) : SymbolBasedAnnotationArgument(name, javac), JavaLiteralAnnotationArgument
