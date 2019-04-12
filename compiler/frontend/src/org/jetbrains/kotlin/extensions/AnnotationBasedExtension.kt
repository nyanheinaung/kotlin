/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.extensions

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass
import org.jetbrains.kotlin.types.TypeUtils

interface AnnotationBasedExtension {
    fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?): List<String>

    fun DeclarationDescriptor.hasSpecialAnnotation(modifierListOwner: KtModifierListOwner?): Boolean {
        val specialAnnotations = getAnnotationFqNames(modifierListOwner).takeIf { it.isNotEmpty() } ?: return false

        if (annotations.any { it.isASpecialAnnotation(specialAnnotations) }) return true

        if (this is ClassDescriptor) {
            for (superType in TypeUtils.getAllSupertypes(defaultType)) {
                val superTypeDescriptor = superType.constructor.declarationDescriptor as? ClassDescriptor ?: continue
                if (superTypeDescriptor.annotations.any { it.isASpecialAnnotation(specialAnnotations) }) return true
            }
        }

        return false
    }

    private fun AnnotationDescriptor.isASpecialAnnotation(
        specialAnnotations: List<String>,
        visitedAnnotations: MutableSet<String> = hashSetOf(),
        allowMetaAnnotations: Boolean = true
    ): Boolean {
        val annotationFqName = fqName?.asString() ?: return false
        if (annotationFqName in visitedAnnotations) return false // Prevent infinite recursion
        if (annotationFqName in specialAnnotations) return true

        visitedAnnotations.add(annotationFqName)

        if (allowMetaAnnotations) {
            val annotationType = annotationClass ?: return false
            for (metaAnnotation in annotationType.annotations) {
                if (metaAnnotation.isASpecialAnnotation(specialAnnotations, visitedAnnotations, allowMetaAnnotations = true)) {
                    return true
                }
            }
        }

        visitedAnnotations.remove(annotationFqName)

        return false
    }
}
