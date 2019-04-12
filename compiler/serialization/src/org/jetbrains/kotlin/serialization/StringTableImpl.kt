/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization

import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName
import org.jetbrains.kotlin.metadata.serialization.Interner
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

open class StringTableImpl : DescriptorAwareStringTable {
    private class FqNameProto(val fqName: QualifiedName.Builder) {
        override fun hashCode(): Int {
            var result = 13
            result = 31 * result + fqName.parentQualifiedName
            result = 31 * result + fqName.shortName
            result = 31 * result + fqName.kind.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is FqNameProto) return false

            val otherFqName = other.fqName
            return fqName.parentQualifiedName == otherFqName.parentQualifiedName
                    && fqName.shortName == otherFqName.shortName
                    && fqName.kind == otherFqName.kind
        }
    }

    private val strings = Interner<String>()
    private val qualifiedNames = Interner<FqNameProto>()

    override fun getStringIndex(string: String): Int = strings.intern(string)

    override fun getQualifiedClassNameIndex(className: String, isLocal: Boolean): Int =
        getQualifiedClassNameIndex(ClassId.fromString(className, isLocal))

    override fun getQualifiedClassNameIndex(classId: ClassId): Int {
        val builder = QualifiedName.newBuilder()
        builder.kind = QualifiedName.Kind.CLASS

        builder.parentQualifiedName =
            classId.outerClassId?.let(this::getQualifiedClassNameIndex)
                ?: getPackageFqNameIndex(classId.packageFqName)

        builder.shortName = getStringIndex(classId.shortClassName.asString())

        return qualifiedNames.intern(FqNameProto(builder))
    }

    fun getPackageFqNameIndex(fqName: FqName): Int {
        var result = -1
        for (segment in fqName.pathSegments()) {
            val builder = QualifiedName.newBuilder()
            builder.shortName = getStringIndex(segment.asString())
            if (result != -1) {
                builder.parentQualifiedName = result
            }
            result = qualifiedNames.intern(FqNameProto(builder))
        }
        return result
    }

    fun buildProto(): Pair<ProtoBuf.StringTable, ProtoBuf.QualifiedNameTable> {
        val strings = ProtoBuf.StringTable.newBuilder()
        for (simpleName in this.strings.allInternedObjects) {
            strings.addString(simpleName)
        }

        val qualifiedNames = ProtoBuf.QualifiedNameTable.newBuilder()
        for (fqName in this.qualifiedNames.allInternedObjects) {
            qualifiedNames.addQualifiedName(fqName.fqName)
        }

        return Pair(strings.build(), qualifiedNames.build())
    }
}
