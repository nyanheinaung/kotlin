/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.patterns.typePredicates

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.util.function.Predicate

interface TypePredicate : Predicate<KotlinType>

private val KOTLIN = TypePredicateImpl("kotlin")
val COMPARABLE: TypePredicate = KOTLIN.inner("Comparable")
val CHAR_SEQUENCE: TypePredicate = KOTLIN.inner("CharSequence")

// TODO: replace all NamePredicate usages to TypePredicate
/**
 * A more precise NamePredicate analog, that checks fully-qualified
 * name rather than simple name.
 *
 * @see org.jetbrains.kotlin.js.patterns.NamePredicate
 */
private class TypePredicateImpl
    private constructor (private val nameParts: List<String>)
: TypePredicate {
    constructor(name: String) : this(listOf(name))

    override fun test(type: KotlinType): Boolean {
        var descriptor: DeclarationDescriptor? = type.constructor.declarationDescriptor ?: return false

        for (i in nameParts.lastIndex downTo 0) {
            if (nameParts[i] != descriptor?.name?.asString()) return false

            descriptor = descriptor.containingDeclaration
        }

        return true
    }

    fun inner(name: String) = TypePredicateImpl(nameParts + listOf(name))
}
