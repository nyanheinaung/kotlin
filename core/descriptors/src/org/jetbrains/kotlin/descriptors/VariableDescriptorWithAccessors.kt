/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

interface VariableDescriptorWithAccessors : VariableDescriptor {
    val getter: VariableAccessorDescriptor?

    val setter: VariableAccessorDescriptor?

    /**
     * Please be careful with this method. Depending on the fact that a property is delegated may be dangerous in the compiler.
     * Whether or not a property is delegated is neither the API or the ABI of that property, and one should be able to recompile a library
     * in a way that makes some non-delegated properties delegated or vice versa, without any problems at compilation time or at runtime.
     *
     * This flag is needed for reflection however, that's why it's serialized to metadata and is exposed in this interface.
     */
    @Deprecated("Do not call this method in the compiler front-end.")
    val isDelegated: Boolean
}

val VariableDescriptorWithAccessors.accessors: List<VariableAccessorDescriptor>
    get() = listOfNotNull(getter, setter)
