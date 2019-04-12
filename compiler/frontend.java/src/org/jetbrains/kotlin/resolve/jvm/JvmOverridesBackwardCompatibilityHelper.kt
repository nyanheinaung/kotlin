/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.builtins.jvm.JavaToKotlinClassMap
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.deserialization.PLATFORM_DEPENDENT_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.load.java.descriptors.JavaMethodDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.OverridesBackwardCompatibilityHelper
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe

object JvmOverridesBackwardCompatibilityHelper : OverridesBackwardCompatibilityHelper {
    override fun overrideCanBeOmitted(overridingDescriptor: CallableMemberDescriptor): Boolean {
        val visitedDescriptors = hashSetOf<CallableMemberDescriptor>()
        return overridingDescriptor.overriddenDescriptors.all {
            isPlatformSpecificDescriptorThatCanBeImplicitlyOverridden(it, visitedDescriptors)
        }
    }

    private fun isPlatformSpecificDescriptorThatCanBeImplicitlyOverridden(
            overriddenDescriptor: CallableMemberDescriptor,
            visitedDescriptors: MutableSet<CallableMemberDescriptor>
    ): Boolean {
        if (overriddenDescriptor.modality == Modality.FINAL) return false

        if (visitedDescriptors.contains(overriddenDescriptor.original)) return true
        visitedDescriptors.add(overriddenDescriptor.original)

        when (overriddenDescriptor.kind) {
            CallableMemberDescriptor.Kind.DELEGATION,
            CallableMemberDescriptor.Kind.FAKE_OVERRIDE ->
                return isOverridingOnlyDescriptorsThatCanBeImplicitlyOverridden(overriddenDescriptor, visitedDescriptors)

            CallableMemberDescriptor.Kind.DECLARATION -> {
                when {
                    overriddenDescriptor.annotations.hasAnnotation(PLATFORM_DEPENDENT_ANNOTATION_FQ_NAME) ->
                        return true
                    overriddenDescriptor is JavaMethodDescriptor -> {
                        val containingClass = DescriptorUtils.getContainingClass(overriddenDescriptor)
                                              ?: return false

                        if (JavaToKotlinClassMap.mapKotlinToJava(containingClass.fqNameUnsafe) != null) return true
                        if (overriddenDescriptor.overriddenDescriptors.isEmpty()) return false

                        return isOverridingOnlyDescriptorsThatCanBeImplicitlyOverridden(overriddenDescriptor, visitedDescriptors)
                    }
                    else ->
                        return false
                }

            }

            else ->
                return false
        }
    }

    private fun isOverridingOnlyDescriptorsThatCanBeImplicitlyOverridden(
            overriddenDescriptor: CallableMemberDescriptor,
            visitedDescriptors: MutableSet<CallableMemberDescriptor>
    ): Boolean =
            overriddenDescriptor.overriddenDescriptors.all {
                isPlatformSpecificDescriptorThatCanBeImplicitlyOverridden(it, visitedDescriptors)
            }

}