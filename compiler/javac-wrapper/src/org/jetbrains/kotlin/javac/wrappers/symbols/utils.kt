/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.symbols

import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.JavaVisibilities
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import javax.lang.model.AnnotatedConstruct
import javax.lang.model.element.*

internal val Element.isAbstract: Boolean
    get() = modifiers.contains(Modifier.ABSTRACT)

internal val Element.isStatic: Boolean
    get() = modifiers.contains(Modifier.STATIC)

internal val Element.isFinal: Boolean
    get() = modifiers.contains(Modifier.FINAL)

internal fun Element.getVisibility(): Visibility = modifiers.let { modifiers ->
    when {
        Modifier.PUBLIC in modifiers -> Visibilities.PUBLIC
        Modifier.PRIVATE in modifiers -> Visibilities.PRIVATE
        Modifier.PROTECTED in modifiers -> {
            if (Modifier.STATIC in modifiers) {
                JavaVisibilities.PROTECTED_STATIC_VISIBILITY
            }
            else {
                JavaVisibilities.PROTECTED_AND_PACKAGE
            }
        }
        else -> JavaVisibilities.PACKAGE_VISIBILITY
    }
}

internal fun TypeElement.computeClassId(): ClassId? {
    val enclosingElement = enclosingElement
    if (enclosingElement.kind != ElementKind.PACKAGE) {
        val parentClassId = (enclosingElement as TypeElement).computeClassId() ?: return null
        return parentClassId.createNestedClassId(Name.identifier(simpleName.toString()))
    }

    return ClassId.topLevel(FqName(qualifiedName.toString()))
}

internal fun ExecutableElement.valueParameters(javac: JavacWrapper): List<JavaValueParameter> =
        parameters.let { parameters ->
            val isVarArgs = isVarArgs
            val lastIndex = parameters.lastIndex
            parameters.mapIndexed { index, parameter ->
                val simpleName = parameter.simpleName.toString()
                SymbolBasedValueParameter(parameter,
                                          if (!simpleName.contentEquals("arg$index")) simpleName else "p$index",
                                          index == lastIndex && isVarArgs,
                                          javac)
            }
        }

internal fun AnnotatedConstruct.findAnnotation(fqName: FqName,
                                               javac: JavacWrapper) =
        annotationMirrors.find {
            (it.annotationType.asElement() as TypeElement).qualifiedName.toString() == fqName.asString()
        }?.let { SymbolBasedAnnotation(it, javac) }