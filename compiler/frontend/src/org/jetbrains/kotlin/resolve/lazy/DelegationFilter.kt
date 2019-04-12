/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor

interface DelegationFilter {

    fun filter(interfaceMember: CallableMemberDescriptor, languageVersionSettings: LanguageVersionSettings): Boolean

    object Default : DelegationFilter {
        override fun filter(interfaceMember: CallableMemberDescriptor, languageVersionSettings: LanguageVersionSettings) = true
    }
}