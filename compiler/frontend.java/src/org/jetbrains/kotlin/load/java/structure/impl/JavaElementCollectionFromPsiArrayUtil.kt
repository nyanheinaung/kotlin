/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("JavaElementCollectionFromPsiArrayUtil")

package org.jetbrains.kotlin.load.java.structure.impl

import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.load.java.NULLABILITY_ANNOTATIONS
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.Name

private inline fun <Psi, Java> Array<Psi>.convert(factory: (Psi) -> Java): List<Java> =
        when (size) {
            0 -> emptyList()
            1 -> listOf(factory(first()))
            else -> map(factory)
        }

private fun <Psi, Java> Collection<Psi>.convert(factory: (Psi) -> Java): List<Java> =
        when (size) {
            0 -> emptyList()
            1 -> listOf(factory(first()))
            else -> map(factory)
        }

internal fun classes(classes: Array<PsiClass>): Collection<JavaClass> =
        classes.convert(::JavaClassImpl)

internal fun classes(classes: Collection<PsiClass>): Collection<JavaClass> =
        classes.convert(::JavaClassImpl)

internal fun packages(packages: Array<PsiPackage>, scope: GlobalSearchScope): Collection<JavaPackage> =
        packages.convert { psi -> JavaPackageImpl(psi, scope) }

internal fun methods(methods: Collection<PsiMethod>): Collection<JavaMethod> =
        methods.convert(::JavaMethodImpl)

internal fun constructors(methods: Collection<PsiMethod>): Collection<JavaConstructor> =
        methods.convert(::JavaConstructorImpl)

internal fun fields(fields: Collection<PsiField>): Collection<JavaField> =
        fields.convert(::JavaFieldImpl)

internal fun valueParameters(parameters: Array<PsiParameter>): List<JavaValueParameter> =
        parameters.convert(::JavaValueParameterImpl)

internal fun typeParameters(typeParameters: Array<PsiTypeParameter>): List<JavaTypeParameter> =
        typeParameters.convert(::JavaTypeParameterImpl)

internal fun classifierTypes(classTypes: Array<PsiClassType>): Collection<JavaClassifierType> =
        classTypes.convert(::JavaClassifierTypeImpl)

internal fun annotations(annotations: Array<out PsiAnnotation>): Collection<JavaAnnotation> =
        annotations.convert(::JavaAnnotationImpl)

internal fun nullabilityAnnotations(annotations: Array<out PsiAnnotation>): Collection<JavaAnnotation> =
        annotations.convert(::JavaAnnotationImpl)
                .filter { annotation ->
                    val fqName = annotation.classId?.asSingleFqName() ?: return@filter false
                    fqName in NULLABILITY_ANNOTATIONS
                }


internal fun namedAnnotationArguments(nameValuePairs: Array<PsiNameValuePair>): Collection<JavaAnnotationArgument> =
        nameValuePairs.convert { psi ->
            val name = psi.name?.let(Name::identifier)
            val value = psi.value ?: error("Annotation argument value cannot be null: $name")
            JavaAnnotationArgumentImpl.create(value, name)
        }
