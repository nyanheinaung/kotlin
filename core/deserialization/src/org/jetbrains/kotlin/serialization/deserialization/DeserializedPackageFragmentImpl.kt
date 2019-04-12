/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.deserialization.NameResolverImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedPackageMemberScope
import org.jetbrains.kotlin.storage.StorageManager

abstract class DeserializedPackageFragmentImpl(
    fqName: FqName,
    storageManager: StorageManager,
    module: ModuleDescriptor,
    proto: ProtoBuf.PackageFragment,
    private val metadataVersion: BinaryVersion,
    private val containerSource: DeserializedContainerSource?
) : DeserializedPackageFragment(fqName, storageManager, module) {
    protected val nameResolver = NameResolverImpl(proto.strings, proto.qualifiedNames)

    override val classDataFinder =
        ProtoBasedClassDataFinder(proto, nameResolver, metadataVersion) { containerSource ?: SourceElement.NO_SOURCE }

    // Temporary storage: until `initialize` is called
    private var _proto: ProtoBuf.PackageFragment? = proto
    private lateinit var _memberScope: MemberScope

    override fun initialize(components: DeserializationComponents) {
        val proto = _proto ?: error("Repeated call to DeserializedPackageFragmentImpl::initialize")
        _proto = null
        _memberScope = DeserializedPackageMemberScope(
            this, proto.`package`, nameResolver, metadataVersion, containerSource, components,
            classNames = {
                classDataFinder.allClassIds.filter { classId ->
                    !classId.isNestedClass && classId !in ClassDeserializer.BLACK_LIST
                }.map { it.shortClassName }
            }
        )
    }

    override fun getMemberScope(): MemberScope = _memberScope
}
