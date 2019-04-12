/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

interface ModuleVisibilityHelper {
    fun isInFriendModule(what: DeclarationDescriptor, from: DeclarationDescriptor): Boolean

    object EMPTY: ModuleVisibilityHelper {
        override fun isInFriendModule(what: DeclarationDescriptor, from: DeclarationDescriptor) = true
    }
}