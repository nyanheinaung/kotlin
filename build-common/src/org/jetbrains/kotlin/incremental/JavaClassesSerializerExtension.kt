/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.JavaVisibilities
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.java.JavaClassProtoBuf
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmMetadataVersion
import org.jetbrains.kotlin.metadata.serialization.MutableVersionRequirementTable
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.serialization.DescriptorSerializer
import org.jetbrains.kotlin.serialization.KotlinSerializerExtensionBase
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInSerializerProtocol

// It uses BuiltInSerializerProtocol for annotations serialization
class JavaClassesSerializerExtension : KotlinSerializerExtensionBase(BuiltInSerializerProtocol) {
    override val metadataVersion: BinaryVersion
        get() = JvmMetadataVersion.INVALID_VERSION

    override fun serializeClass(
            descriptor: ClassDescriptor,
            proto: ProtoBuf.Class.Builder,
            versionRequirementTable: MutableVersionRequirementTable,
            childSerializer: DescriptorSerializer
    ) {
        super.serializeClass(descriptor, proto, versionRequirementTable, childSerializer)
        if (descriptor.visibility == JavaVisibilities.PACKAGE_VISIBILITY) {
            proto.setExtension(JavaClassProtoBuf.isPackagePrivateClass, true)
        }
    }

    override fun serializeConstructor(descriptor: ConstructorDescriptor,
                                      proto: ProtoBuf.Constructor.Builder,
                                      childSerializer: DescriptorSerializer) {
        super.serializeConstructor(descriptor, proto, childSerializer)
        if (descriptor.visibility == JavaVisibilities.PACKAGE_VISIBILITY) {
            proto.setExtension(JavaClassProtoBuf.isPackagePrivateConstructor, true)
        }
    }

    override fun serializeFunction(descriptor: FunctionDescriptor,
                                   proto: ProtoBuf.Function.Builder,
                                   childSerializer: DescriptorSerializer) {
        super.serializeFunction(descriptor, proto, childSerializer)
        if (descriptor.visibility == JavaVisibilities.PACKAGE_VISIBILITY) {
            proto.setExtension(JavaClassProtoBuf.isPackagePrivateMethod, true)
        }

        if (descriptor.dispatchReceiverParameter == null) {
            proto.setExtension(JavaClassProtoBuf.isStaticMethod, true)
        }
    }

    override fun serializeProperty(
            descriptor: PropertyDescriptor,
            proto: ProtoBuf.Property.Builder,
            versionRequirementTable: MutableVersionRequirementTable?,
            childSerializer: DescriptorSerializer
    ) {
        super.serializeProperty(descriptor, proto, versionRequirementTable, childSerializer)
        if (descriptor.visibility == JavaVisibilities.PACKAGE_VISIBILITY) {
            proto.setExtension(JavaClassProtoBuf.isPackagePrivateField, true)
        }

        if (descriptor.dispatchReceiverParameter == null) {
            proto.setExtension(JavaClassProtoBuf.isStaticField, true)
        }
    }

    override fun shouldUseNormalizedVisibility() = true

    override val customClassMembersProducer =
            object : ClassMembersProducer {
                override fun getCallableMembers(classDescriptor: ClassDescriptor) =
                        arrayListOf<CallableMemberDescriptor>().apply {
                            addAll(classDescriptor.unsubstitutedMemberScope.getSortedCallableDescriptors())
                            addAll(classDescriptor.staticScope.getSortedCallableDescriptors())
                        }
            }

    private fun MemberScope.getSortedCallableDescriptors(): Collection<CallableMemberDescriptor> =
            DescriptorUtils.getAllDescriptors(this).filterIsInstance<CallableMemberDescriptor>()
                    .let { DescriptorSerializer.sort(it) }
}
