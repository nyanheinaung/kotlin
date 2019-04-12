/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.AnnotationChecker
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

/*
    This class lazily splits Annotations into several different Annotations according to declaration site target priority on property.
    Basically you pass an original Annotations and a list of targets applicable for your declaration.

    Example: Annotations = [@property:P1, @field:F1, F2, P2, T],
             F2, P2, T are annotations with declaration site targets 'field', 'property' and 'type', respectively.

    Annotations will be split as:
             FIELD -> [F1, F2], (even if F2 is applicable to PROPERTY because of target priority)
             PROPERTY -> [P1, P2],
             other -> [T].
 */

class AnnotationSplitter(
    private val storageManager: StorageManager,
    allAnnotations: Annotations,
    applicableTargets: Set<AnnotationUseSiteTarget>
) {
    companion object {
        private val TARGET_PRIORITIES = setOf(CONSTRUCTOR_PARAMETER, PROPERTY, FIELD)
    }

    private val splitAnnotations = storageManager.createLazyValue {
        val map = hashMapOf<AnnotationUseSiteTarget, MutableList<AnnotationDescriptor>>()
        val other = arrayListOf<AnnotationDescriptor>()
        val applicableTargetsWithoutUseSiteTarget = applicableTargets.intersect(TARGET_PRIORITIES)

        outer@ for (annotation in allAnnotations) {
            for (target in TARGET_PRIORITIES) {
                if (target !in applicableTargetsWithoutUseSiteTarget) continue

                val declarationSiteTargetForCurrentTarget = KotlinTarget.USE_SITE_MAPPING[target] ?: continue
                val applicableTargetsForAnnotation = AnnotationChecker.applicableTargetSet(annotation)

                if (declarationSiteTargetForCurrentTarget in applicableTargetsForAnnotation) {
                    map.getOrPut(target) { arrayListOf() }.add(annotation)
                    continue@outer
                }
            }

            other.add(annotation)
        }

        for ((annotation, target) in @Suppress("DEPRECATION") allAnnotations.getUseSiteTargetedAnnotations()) {
            if (target in applicableTargets) {
                map.getOrPut(target) { arrayListOf() }.add(annotation)
            }
        }

        map to Annotations.create(other)
    }

    fun getOtherAnnotations(): Annotations = LazySplitAnnotations(storageManager, null)

    fun getAnnotationsForTarget(target: AnnotationUseSiteTarget): Annotations = LazySplitAnnotations(storageManager, target)

    private inner class LazySplitAnnotations(
        storageManager: StorageManager,
        val target: AnnotationUseSiteTarget?
    ) : Annotations, LazyEntity {
        private val annotations by storageManager.createLazyValue {
            val (targeted, other) = this@AnnotationSplitter.splitAnnotations()

            if (target != null) {
                targeted[target]?.let((Annotations)::create) ?: Annotations.EMPTY
            } else {
                other
            }
        }

        override fun forceResolveAllContents() {
            for (annotation in this) {
                // TODO: probably we should do ForceResolveUtil.forceResolveAllContents(annotation) here
            }
        }

        override fun isEmpty() = annotations.isEmpty()
        override fun hasAnnotation(fqName: FqName) = annotations.hasAnnotation(fqName)
        override fun findAnnotation(fqName: FqName) = annotations.findAnnotation(fqName)
        override fun iterator() = annotations.iterator()
        override fun toString() = annotations.toString()
    }
}
