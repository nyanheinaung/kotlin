/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities

fun findMemberWithMaxVisibility(descriptors: Collection<CallableMemberDescriptor>): CallableMemberDescriptor {
    assert(descriptors.isNotEmpty())

    var descriptor: CallableMemberDescriptor? = null
    for (candidate in descriptors) {
        if (descriptor == null) {
            descriptor = candidate
            continue
        }

        val result = Visibilities.compare(descriptor.visibility, candidate.visibility)
        if (result != null && result < 0) {
            descriptor = candidate
        }
    }
    return descriptor!!
}
