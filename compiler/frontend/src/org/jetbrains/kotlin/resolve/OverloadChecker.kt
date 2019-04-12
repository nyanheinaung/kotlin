/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.calls.inference.ConstraintSystemBuilderImpl
import org.jetbrains.kotlin.resolve.calls.results.FlatSignature
import org.jetbrains.kotlin.resolve.calls.results.SpecificityComparisonCallbacks
import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.resolve.calls.results.isSignatureNotLessSpecific
import org.jetbrains.kotlin.resolve.descriptorUtil.hasLowPriorityInOverloadResolution
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtensionProperty
import org.jetbrains.kotlin.resolve.descriptorUtil.varargParameterPosition
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType

object OverloadabilitySpecificityCallbacks : SpecificityComparisonCallbacks {
    override fun isNonSubtypeNotLessSpecific(specific: KotlinType, general: KotlinType): Boolean =
        false
}

class OverloadChecker(val specificityComparator: TypeSpecificityComparator) {
    /**
     * Does not check names.
     */
    fun isOverloadable(a: DeclarationDescriptor, b: DeclarationDescriptor): Boolean {
        val aCategory = getDeclarationCategory(a)
        val bCategory = getDeclarationCategory(b)

        if (aCategory != bCategory) return true
        if (a !is CallableDescriptor || b !is CallableDescriptor) return false

        return checkOverloadability(a, b)
    }

    private fun checkOverloadability(a: CallableDescriptor, b: CallableDescriptor): Boolean {
        if (a.hasLowPriorityInOverloadResolution() != b.hasLowPriorityInOverloadResolution()) return true

        // NB this makes generic and non-generic declarations with equivalent signatures non-conflicting
        // E.g., 'fun <T> foo()' and 'fun foo()'.
        // They can be disambiguated by providing explicit type parameters.
        if (a.typeParameters.isEmpty() != b.typeParameters.isEmpty()) return true

        if (a is FunctionDescriptor && ErrorUtils.containsErrorTypeInParameters(a) ||
            b is FunctionDescriptor && ErrorUtils.containsErrorTypeInParameters(b)
        ) return true
        if (a.varargParameterPosition() != b.varargParameterPosition()) return true

        val aSignature = FlatSignature.createFromCallableDescriptor(a)
        val bSignature = FlatSignature.createFromCallableDescriptor(b)

        val aIsNotLessSpecificThanB = ConstraintSystemBuilderImpl.forSpecificity()
            .isSignatureNotLessSpecific(aSignature, bSignature, OverloadabilitySpecificityCallbacks, specificityComparator)
        val bIsNotLessSpecificThanA = ConstraintSystemBuilderImpl.forSpecificity()
            .isSignatureNotLessSpecific(bSignature, aSignature, OverloadabilitySpecificityCallbacks, specificityComparator)

        return !(aIsNotLessSpecificThanB && bIsNotLessSpecificThanA)
    }

    private enum class DeclarationCategory {
        TYPE_OR_VALUE,
        FUNCTION,
        EXTENSION_PROPERTY
    }

    private fun getDeclarationCategory(a: DeclarationDescriptor): DeclarationCategory =
        when (a) {
            is PropertyDescriptor ->
                if (a.isExtensionProperty)
                    DeclarationCategory.EXTENSION_PROPERTY
                else
                    DeclarationCategory.TYPE_OR_VALUE
            is FunctionDescriptor ->
                DeclarationCategory.FUNCTION
            is ClassifierDescriptor ->
                DeclarationCategory.TYPE_OR_VALUE
            else ->
                error("Unexpected declaration kind: $a")
        }

}
