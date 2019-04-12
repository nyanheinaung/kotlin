/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.components

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.serialization.deserialization.ErrorReporter

object RuntimeErrorReporter : ErrorReporter {
    // TODO: specialized exceptions
    override fun reportIncompleteHierarchy(descriptor: ClassDescriptor, unresolvedSuperClasses: MutableList<String>) {
        throw IllegalStateException("Incomplete hierarchy for class ${descriptor.name}, unresolved classes $unresolvedSuperClasses")
    }

    override fun reportCannotInferVisibility(descriptor: CallableMemberDescriptor) {
        // TODO: use DescriptorRenderer
        throw IllegalStateException("Cannot infer visibility for $descriptor")
    }
}
