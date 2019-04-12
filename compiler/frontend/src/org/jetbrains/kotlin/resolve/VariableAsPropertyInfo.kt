/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.types.KotlinType

class VariableAsPropertyInfo(
    val propertyGetter: KtPropertyAccessor?,
    val propertySetter: KtPropertyAccessor?,
    val variableType: KotlinType?,
    val hasBody: Boolean,
    val hasDelegate: Boolean
) {
    companion object {
        fun createFromDestructuringDeclarationEntry(type: KotlinType): VariableAsPropertyInfo {
            return VariableAsPropertyInfo(null, null, type, false, false)
        }

        fun createFromProperty(property: KtProperty): VariableAsPropertyInfo {
            return VariableAsPropertyInfo(property.getter, property.setter, null, property.hasBody(), property.hasDelegate())
        }
    }
}