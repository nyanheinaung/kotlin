/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.TypeTable
import org.jetbrains.kotlin.metadata.deserialization.getExtensionOrNull
import org.jetbrains.kotlin.metadata.jvm.JvmProtoBuf
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmMetadataVersion
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmNameResolver
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.serialization.deserialization.MemberDeserializer
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.internal.KDeclarationContainerImpl.MemberBelonginess.DECLARED
import kotlin.reflect.jvm.internal.components.ReflectKotlinClass
import kotlin.reflect.jvm.internal.structure.classId

internal class KPackageImpl(
    override val jClass: Class<*>,
    @Suppress("unused") val usageModuleName: String? = null // may be useful for debug
) : KDeclarationContainerImpl() {
    private inner class Data : KDeclarationContainerImpl.Data() {
        private val kotlinClass: ReflectKotlinClass? by ReflectProperties.lazySoft {
            ReflectKotlinClass.create(jClass)
        }

        val scope: MemberScope by ReflectProperties.lazySoft {
            val klass = kotlinClass

            if (klass != null)
                moduleData.packagePartScopeCache.getPackagePartScope(klass)
            else MemberScope.Empty
        }

        val multifileFacade: Class<*>? by ReflectProperties.lazy {
            val facadeName = kotlinClass?.classHeader?.multifileClassName
            // We need to check isNotEmpty because this is the value read from the annotation which cannot be null.
            // The default value for 'xs' is empty string, as declared in kotlin.Metadata
            if (facadeName != null && facadeName.isNotEmpty())
                jClass.classLoader.loadClass(facadeName.replace('/', '.'))
            else null
        }

        val metadata: Triple<JvmNameResolver, ProtoBuf.Package, JvmMetadataVersion>? by ReflectProperties.lazy {
            kotlinClass?.classHeader?.let { header ->
                val data = header.data
                val strings = header.strings
                if (data != null && strings != null) {
                    val (nameResolver, proto) = JvmProtoBufUtil.readPackageDataFrom(data, strings)
                    Triple(nameResolver, proto, header.metadataVersion)
                } else null
            }
        }

        val members: Collection<KCallableImpl<*>> by ReflectProperties.lazySoft {
            getMembers(scope, DECLARED)
        }
    }

    private val data = ReflectProperties.lazy { Data() }

    override val methodOwner: Class<*> get() = data().multifileFacade ?: jClass

    private val scope: MemberScope get() = data().scope

    override val members: Collection<KCallable<*>> get() = data().members

    override val constructorDescriptors: Collection<ConstructorDescriptor>
        get() = emptyList()

    override fun getProperties(name: Name): Collection<PropertyDescriptor> =
        scope.getContributedVariables(name, NoLookupLocation.FROM_REFLECTION)

    override fun getFunctions(name: Name): Collection<FunctionDescriptor> =
        scope.getContributedFunctions(name, NoLookupLocation.FROM_REFLECTION)

    override fun getLocalProperty(index: Int): PropertyDescriptor? {
        return data().metadata?.let { (nameResolver, packageProto, metadataVersion) ->
            packageProto.getExtensionOrNull(JvmProtoBuf.packageLocalVariable, index)?.let { proto ->
                deserializeToDescriptor(
                    jClass, proto, nameResolver, TypeTable(packageProto.typeTable), metadataVersion,
                    MemberDeserializer::loadProperty
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        other is KPackageImpl && jClass == other.jClass

    override fun hashCode(): Int =
        jClass.hashCode()

    override fun toString(): String =
        "file class ${jClass.classId.asSingleFqName()}"
}
