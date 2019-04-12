/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.deserialization.AdditionalClassPartsProvider
import org.jetbrains.kotlin.descriptors.deserialization.ClassDescriptorFactory
import org.jetbrains.kotlin.descriptors.deserialization.PlatformDependentDeclarationFilter
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.protobuf.ExtensionRegistryLite
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource
import org.jetbrains.kotlin.storage.StorageManager

class DeserializationComponents(
    val storageManager: StorageManager,
    val moduleDescriptor: ModuleDescriptor,
    val configuration: DeserializationConfiguration,
    val classDataFinder: ClassDataFinder,
    val annotationAndConstantLoader: AnnotationAndConstantLoader<AnnotationDescriptor, ConstantValue<*>>,
    val packageFragmentProvider: PackageFragmentProvider,
    val localClassifierTypeSettings: LocalClassifierTypeSettings,
    val errorReporter: ErrorReporter,
    val lookupTracker: LookupTracker,
    val flexibleTypeDeserializer: FlexibleTypeDeserializer,
    val fictitiousClassDescriptorFactories: Iterable<ClassDescriptorFactory>,
    val notFoundClasses: NotFoundClasses,
    val contractDeserializer: ContractDeserializer,
    val additionalClassPartsProvider: AdditionalClassPartsProvider = AdditionalClassPartsProvider.None,
    val platformDependentDeclarationFilter: PlatformDependentDeclarationFilter = PlatformDependentDeclarationFilter.All,
    val extensionRegistryLite: ExtensionRegistryLite
) {
    val classDeserializer: ClassDeserializer = ClassDeserializer(this)

    fun deserializeClass(classId: ClassId): ClassDescriptor? = classDeserializer.deserializeClass(classId)

    fun createContext(
        descriptor: PackageFragmentDescriptor,
        nameResolver: NameResolver,
        typeTable: TypeTable,
        versionRequirementTable: VersionRequirementTable,
        metadataVersion: BinaryVersion,
        containerSource: DeserializedContainerSource?
    ): DeserializationContext =
        DeserializationContext(
            this, nameResolver, descriptor, typeTable, versionRequirementTable, metadataVersion, containerSource,
            parentTypeDeserializer = null, typeParameters = listOf()
        )
}


class DeserializationContext(
    val components: DeserializationComponents,
    val nameResolver: NameResolver,
    val containingDeclaration: DeclarationDescriptor,
    val typeTable: TypeTable,
    val versionRequirementTable: VersionRequirementTable,
    val metadataVersion: BinaryVersion,
    val containerSource: DeserializedContainerSource?,
    parentTypeDeserializer: TypeDeserializer?,
    typeParameters: List<ProtoBuf.TypeParameter>
) {
    val typeDeserializer: TypeDeserializer = TypeDeserializer(
        this, parentTypeDeserializer, typeParameters,
        "Deserializer for \"${containingDeclaration.name}\"",
        containerSource?.presentableString ?: "[container not found]"
    )

    val memberDeserializer: MemberDeserializer = MemberDeserializer(this)

    val storageManager: StorageManager get() = components.storageManager

    fun childContext(
        descriptor: DeclarationDescriptor,
        typeParameterProtos: List<ProtoBuf.TypeParameter>,
        nameResolver: NameResolver = this.nameResolver,
        typeTable: TypeTable = this.typeTable,
        versionRequirementTable: VersionRequirementTable = this.versionRequirementTable,
        metadataVersion: BinaryVersion = this.metadataVersion
    ): DeserializationContext = DeserializationContext(
        components, nameResolver, descriptor, typeTable,
        if (isVersionRequirementTableWrittenCorrectly(metadataVersion)) versionRequirementTable else this.versionRequirementTable,
        metadataVersion, this.containerSource,
        parentTypeDeserializer = this.typeDeserializer, typeParameters = typeParameterProtos
    )
}
