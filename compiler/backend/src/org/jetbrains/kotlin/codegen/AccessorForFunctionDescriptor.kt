/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.coroutines.INITIAL_DESCRIPTOR_FOR_SUSPEND_FUNCTION
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.FunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name
import java.util.*

class AccessorForFunctionDescriptor(
    override val calleeDescriptor: FunctionDescriptor,
    containingDeclaration: DeclarationDescriptor,
    override val superCallTarget: ClassDescriptor?,
    private val nameSuffix: String,
    override val accessorKind: AccessorKind
) : AbstractAccessorForFunctionDescriptor(containingDeclaration, Name.identifier("access$$nameSuffix")),
    AccessorForCallableDescriptor<FunctionDescriptor> {

    init {
        initialize(
            calleeDescriptor.extensionReceiverParameter?.copy(this),
            if (calleeDescriptor is ConstructorDescriptor || calleeDescriptor.isJvmStaticInObjectOrClassOrInterface())
                null
            else
                calleeDescriptor.dispatchReceiverParameter,
            copyTypeParameters(calleeDescriptor),
            copyValueParameters(calleeDescriptor),
            calleeDescriptor.returnType,
            Modality.FINAL,
            Visibilities.LOCAL
        )

        isSuspend = calleeDescriptor.isSuspend
        if (calleeDescriptor.getUserData(INITIAL_DESCRIPTOR_FOR_SUSPEND_FUNCTION) != null) {
            userDataMap = LinkedHashMap<CallableDescriptor.UserDataKey<*>, Any>()
            userDataMap[INITIAL_DESCRIPTOR_FOR_SUSPEND_FUNCTION] =
                    calleeDescriptor.getUserData(INITIAL_DESCRIPTOR_FOR_SUSPEND_FUNCTION)
        }
    }

    override fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor,
        original: FunctionDescriptor?,
        kind: CallableMemberDescriptor.Kind,
        newName: Name?,
        annotations: Annotations,
        source: SourceElement
    ): FunctionDescriptorImpl {
        return AccessorForFunctionDescriptor(calleeDescriptor, newOwner, superCallTarget, nameSuffix, accessorKind)
    }
}
