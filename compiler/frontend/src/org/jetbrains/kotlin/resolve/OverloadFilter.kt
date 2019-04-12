/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorNonRoot


interface OverloadFilter {
    fun filterPackageMemberOverloads(overloads: Collection<DeclarationDescriptorNonRoot>): Collection<DeclarationDescriptorNonRoot>

    object Default : OverloadFilter {
        override fun filterPackageMemberOverloads(overloads: Collection<DeclarationDescriptorNonRoot>): Collection<DeclarationDescriptorNonRoot> =
            overloads
    }
}