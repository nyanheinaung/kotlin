/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import java.util.HashMap
import java.util.HashSet

class InlineResult private constructor() {

    private val notChangedTypes = hashSetOf<String>()
    private val classesToRemove = HashSet<String>()
    private val changedTypes = HashMap<String, String>()
    val reifiedTypeParametersUsages = ReifiedTypeParametersUsages()

    fun merge(child: InlineResult) {
        classesToRemove.addAll(child.calcClassesToRemove())
    }

    fun mergeWithNotChangeInfo(child: InlineResult) {
        notChangedTypes.addAll(child.notChangedTypes)
        merge(child)
    }

    fun addClassToRemove(classInternalName: String) {
        classesToRemove.add(classInternalName)
    }

    fun addNotChangedClass(classInternalName: String) {
        notChangedTypes.add(classInternalName)
    }

    fun addChangedType(oldClassInternalName: String, newClassInternalName: String) {
        changedTypes.put(oldClassInternalName, newClassInternalName)
    }


    fun calcClassesToRemove(): Set<String> {
        return classesToRemove - notChangedTypes
    }

    fun getChangedTypes(): Map<String, String> {
        return changedTypes
    }

    companion object {
        @JvmStatic
        fun create(): InlineResult {
            return InlineResult()
        }
    }
}
