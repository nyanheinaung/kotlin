/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.deserialization.NameResolver
import org.jetbrains.kotlin.name.ClassId

class ProtoBasedClassDataFinder(
    proto: ProtoBuf.PackageFragment,
    private val nameResolver: NameResolver,
    private val metadataVersion: BinaryVersion,
    private val classSource: (ClassId) -> SourceElement = { SourceElement.NO_SOURCE }
) : ClassDataFinder {
    private val classIdToProto =
        proto.class_List.associateBy { klass ->
            nameResolver.getClassId(klass.fqName)
        }

    val allClassIds: Collection<ClassId> get() = classIdToProto.keys

    override fun findClassData(classId: ClassId): ClassData? {
        val classProto = classIdToProto[classId] ?: return null
        return ClassData(nameResolver, classProto, metadataVersion, classSource(classId))
    }
}
