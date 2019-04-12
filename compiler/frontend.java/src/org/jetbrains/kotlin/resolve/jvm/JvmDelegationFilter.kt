/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.deserialization.PLATFORM_DEPENDENT_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.load.java.descriptors.JavaMethodDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.jvm.annotations.hasJvmDefaultAnnotation
import org.jetbrains.kotlin.resolve.lazy.DelegationFilter

object JvmDelegationFilter : DelegationFilter {

    override fun filter(interfaceMember: CallableMemberDescriptor, languageVersionSettings: LanguageVersionSettings): Boolean {
        if (!languageVersionSettings.supportsFeature(LanguageFeature.NoDelegationToJavaDefaultInterfaceMembers)) return true

        //We always have only one implementation otherwise it's an error in kotlin and java
        val realMember = DescriptorUtils.unwrapFakeOverride(interfaceMember)
        return !isJavaDefaultMethod(realMember) &&
                !realMember.hasJvmDefaultAnnotation() &&
                !isBuiltInMemberMappedToJavaDefault(realMember)
    }

    private fun isJavaDefaultMethod(interfaceMember: CallableMemberDescriptor): Boolean {
        return interfaceMember is JavaMethodDescriptor && interfaceMember.modality != Modality.ABSTRACT
    }

    private fun isBuiltInMemberMappedToJavaDefault(interfaceMember: CallableMemberDescriptor): Boolean {
        return interfaceMember.modality != Modality.ABSTRACT &&
               KotlinBuiltIns.isBuiltIn(interfaceMember) &&
               interfaceMember.annotations.hasAnnotation(PLATFORM_DEPENDENT_ANNOTATION_FQ_NAME)
    }
}
