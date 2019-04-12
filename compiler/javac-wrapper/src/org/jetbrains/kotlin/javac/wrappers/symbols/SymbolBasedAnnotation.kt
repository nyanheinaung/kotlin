/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import com.sun.tools.javac.code.Symbol
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument
import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

open class SymbolBasedAnnotation(
        val annotationMirror: AnnotationMirror,
        val javac: JavacWrapper
) : JavaElement, JavaAnnotation {

    override val arguments: Collection<JavaAnnotationArgument>
        get() = annotationMirror.elementValues.map { (key, value) ->
            SymbolBasedAnnotationArgument.create(value.value, Name.identifier(key.simpleName.toString()), javac)
        }

    override val classId: ClassId
        get() = (annotationMirror.annotationType.asElement() as TypeElement).computeClassId()!!

    override fun resolve() = with(annotationMirror.annotationType.asElement() as Symbol.ClassSymbol) { javac.findClass(classId) }

}