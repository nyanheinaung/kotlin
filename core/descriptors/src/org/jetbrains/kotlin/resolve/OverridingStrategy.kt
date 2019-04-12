/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor

abstract class OverridingStrategy {
    abstract fun addFakeOverride(fakeOverride: CallableMemberDescriptor)

    abstract fun overrideConflict(fromSuper: CallableMemberDescriptor, fromCurrent: CallableMemberDescriptor)

    abstract fun inheritanceConflict(first: CallableMemberDescriptor, second: CallableMemberDescriptor)

    open fun setOverriddenDescriptors(member: CallableMemberDescriptor, overridden: Collection<CallableMemberDescriptor>) {
        member.overriddenDescriptors = overridden
    }
}

abstract class NonReportingOverrideStrategy : OverridingStrategy() {
    override fun overrideConflict(fromSuper: CallableMemberDescriptor, fromCurrent: CallableMemberDescriptor) {
        conflict(fromSuper, fromCurrent)
    }

    override fun inheritanceConflict(first: CallableMemberDescriptor, second: CallableMemberDescriptor) {
        conflict(first, second)
    }

    protected abstract fun conflict(fromSuper: CallableMemberDescriptor, fromCurrent: CallableMemberDescriptor)
}
