/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types


// To facilitate laziness, any KotlinType implementation may inherit from this trait,
// even if it turns out that the type an instance represents is not actually a type variable
// (i.e. it is not derived from a type parameter), see isTypeVariable
interface CustomTypeVariable {
    val isTypeVariable: Boolean

    // Throws an exception when isTypeVariable == false
    fun substitutionResult(replacement: KotlinType): KotlinType
}

fun KotlinType.isCustomTypeVariable(): Boolean = (unwrap() as? CustomTypeVariable)?.isTypeVariable ?: false
fun KotlinType.getCustomTypeVariable(): CustomTypeVariable? =
        (unwrap() as? CustomTypeVariable)?.let {
            if (it.isTypeVariable) it else null
        }

interface SubtypingRepresentatives {
    val subTypeRepresentative: KotlinType
    val superTypeRepresentative: KotlinType

    fun sameTypeConstructor(type: KotlinType): Boolean
}

fun KotlinType.getSubtypeRepresentative(): KotlinType =
        (unwrap() as? SubtypingRepresentatives)?.subTypeRepresentative ?: this

fun KotlinType.getSupertypeRepresentative(): KotlinType =
        (unwrap() as? SubtypingRepresentatives)?.superTypeRepresentative ?: this

fun sameTypeConstructors(first: KotlinType, second: KotlinType): Boolean {
    return (first.unwrap() as? SubtypingRepresentatives)?.sameTypeConstructor(second) ?: false
           || (second.unwrap() as? SubtypingRepresentatives)?.sameTypeConstructor(first) ?: false
}

