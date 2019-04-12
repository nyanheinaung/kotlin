/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.checker

import org.jetbrains.kotlin.resolve.constants.IntegerLiteralTypeConstructor
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker.transformToNewType
import org.jetbrains.kotlin.types.model.KotlinTypeMarker
import org.jetbrains.kotlin.types.model.SimpleTypeMarker
import org.jetbrains.kotlin.types.model.TypeConstructorMarker

open class ClassicTypeCheckerContext(val errorTypeEqualsToAnything: Boolean, val allowedTypeVariable: Boolean = true) : ClassicTypeSystemContext, AbstractTypeCheckerContext() {

    override fun prepareType(type: KotlinTypeMarker): KotlinTypeMarker {
        return transformToNewType((type as KotlinType).unwrap())
    }

    override val isErrorTypeEqualsToAnything: Boolean
        get() = errorTypeEqualsToAnything

    override fun areEqualTypeConstructors(a: TypeConstructorMarker, b: TypeConstructorMarker): Boolean {
        require(a is TypeConstructor, a::errorMessage)
        require(b is TypeConstructor, b::errorMessage)
        return areEqualTypeConstructors(a, b)
    }

    open fun areEqualTypeConstructors(a: TypeConstructor, b: TypeConstructor): Boolean = when {
        /*
         * For integer literal types we have special rules for constructor's equality,
         *   so we have to check it manually
         * For example: Int in ILT.possibleTypes -> ILT == Int
         */
        a is IntegerLiteralTypeConstructor -> a.checkConstructor(b)
        b is IntegerLiteralTypeConstructor -> b.checkConstructor(a)
        else -> a == b
    }

    override fun substitutionSupertypePolicy(type: SimpleTypeMarker): SupertypesPolicy.DoCustomTransform {
        return classicSubstitutionSupertypePolicy(type)
    }

    override val KotlinTypeMarker.isAllowedTypeVariable: Boolean get() = this is UnwrappedType && allowedTypeVariable && constructor is NewTypeVariableConstructor

    companion object {
        fun ClassicTypeSystemContext.classicSubstitutionSupertypePolicy(type: SimpleTypeMarker): SupertypesPolicy.DoCustomTransform {
            require(type is SimpleType, type::errorMessage)
            val substitutor = TypeConstructorSubstitution.create(type).buildSubstitutor()

            return object : SupertypesPolicy.DoCustomTransform() {
                override fun transformType(context: AbstractTypeCheckerContext, type: KotlinTypeMarker): SimpleTypeMarker {
                    return substitutor.safeSubstitute(
                        type.lowerBoundIfFlexible() as KotlinType,
                        Variance.INVARIANT
                    ).asSimpleType()!!
                }
            }
        }
    }
}

private fun Any.errorMessage(): String {
    return "ClassicTypeCheckerContext couldn't handle ${this::class} $this"
}