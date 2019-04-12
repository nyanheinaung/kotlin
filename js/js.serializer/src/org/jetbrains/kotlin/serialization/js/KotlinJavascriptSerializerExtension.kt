/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.js

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.metadata.js.JsProtoBuf
import org.jetbrains.kotlin.metadata.serialization.MutableVersionRequirementTable
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.source.PsiSourceFile
import org.jetbrains.kotlin.serialization.DescriptorSerializer
import org.jetbrains.kotlin.serialization.KotlinSerializerExtensionBase
import org.jetbrains.kotlin.types.FlexibleType

class KotlinJavascriptSerializerExtension(
    private val fileRegistry: KotlinFileRegistry,
    private val languageVersionSettings: LanguageVersionSettings,
    override val metadataVersion: BinaryVersion
) : KotlinSerializerExtensionBase(JsSerializerProtocol) {
    override val stringTable = JavaScriptStringTable()

    override fun serializeFlexibleType(flexibleType: FlexibleType, lowerProto: ProtoBuf.Type.Builder, upperProto: ProtoBuf.Type.Builder) {
        lowerProto.flexibleTypeCapabilitiesId = stringTable.getStringIndex(DynamicTypeDeserializer.id)
    }

    override fun serializeClass(
            descriptor: ClassDescriptor,
            proto: ProtoBuf.Class.Builder,
            versionRequirementTable: MutableVersionRequirementTable,
            childSerializer: DescriptorSerializer
    ) {
        val id = getFileId(descriptor)
        if (id != null) {
            proto.setExtension(JsProtoBuf.classContainingFileId, id)
        }
        super.serializeClass(descriptor, proto, versionRequirementTable, childSerializer)
    }

    override fun serializeProperty(
            descriptor: PropertyDescriptor,
            proto: ProtoBuf.Property.Builder,
            versionRequirementTable: MutableVersionRequirementTable?,
            childSerializer: DescriptorSerializer
    ) {
        val id = getFileId(descriptor)
        if (id != null) {
            proto.setExtension(JsProtoBuf.propertyContainingFileId, id)
        }
        super.serializeProperty(descriptor, proto, versionRequirementTable, childSerializer)
    }

    override fun serializeFunction(descriptor: FunctionDescriptor,
                                   proto: ProtoBuf.Function.Builder,
                                   childSerializer: DescriptorSerializer) {
        val id = getFileId(descriptor)
        if (id != null) {
            proto.setExtension(JsProtoBuf.functionContainingFileId, id)
        }
        super.serializeFunction(descriptor, proto, childSerializer)
    }

    private fun getFileId(descriptor: DeclarationDescriptor): Int? {
        if (!DescriptorUtils.isTopLevelDeclaration(descriptor) || descriptor !is DeclarationDescriptorWithSource) return null

        val fileId = descriptor.extractFileId()
        if (fileId != null) {
            (descriptor.containingDeclaration as? KotlinJavascriptPackageFragment)?.let { packageFragment ->
                return fileRegistry.lookup(KotlinDeserializedFileMetadata(packageFragment, fileId))
            }
        }

        val file = descriptor.source.containingFile as? PsiSourceFile ?: return null

        val psiFile = file.psiFile
        return (psiFile as? KtFile)?.let { fileRegistry.lookup(KotlinPsiFileMetadata(it)) }
    }

    override fun releaseCoroutines() = languageVersionSettings.supportsFeature(LanguageFeature.ReleaseCoroutines)
}
