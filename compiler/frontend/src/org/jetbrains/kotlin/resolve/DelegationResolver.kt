/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind.DELEGATION
import org.jetbrains.kotlin.diagnostics.Errors.MANY_IMPL_MEMBER_NOT_IMPLEMENTED
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.OverridingUtil.OverrideCompatibilityInfo.Result.OVERRIDABLE
import org.jetbrains.kotlin.resolve.lazy.DelegationFilter
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.utils.keysToMapExceptNulls

class DelegationResolver<T : CallableMemberDescriptor> private constructor(
    private val classOrObject: KtPureClassOrObject,
    private val ownerDescriptor: ClassDescriptor,
    private val existingMembers: Collection<CallableDescriptor>,
    private val trace: BindingTrace,
    private val memberExtractor: MemberExtractor<T>,
    private val typeResolver: TypeResolver,
    private val delegationFilter: DelegationFilter,
    private val languageVersionSettings: LanguageVersionSettings
) {

    private fun generateDelegatedMembers(): Collection<T> {
        val delegatedMembers = hashSetOf<T>()
        for (delegationSpecifier in classOrObject.superTypeListEntries) {
            if (delegationSpecifier !is KtDelegatedSuperTypeEntry) {
                continue
            }
            val typeReference = delegationSpecifier.typeReference ?: continue
            val delegatedInterfaceType = typeResolver.resolve(typeReference)
            if (delegatedInterfaceType == null || delegatedInterfaceType.isError) {
                continue
            }
            val delegatesForInterface = generateDelegatesForInterface(delegatedMembers, delegatedInterfaceType)
            delegatedMembers.addAll(delegatesForInterface)
        }
        return delegatedMembers
    }

    private fun generateDelegatesForInterface(existingDelegates: Collection<T>, delegatedInterfaceType: KotlinType): Collection<T> =
        generateDelegationCandidates(delegatedInterfaceType).filter { candidate ->
            !isOverridingAnyOf(candidate, existingMembers) &&
                    !checkClashWithOtherDelegatedMember(candidate, existingDelegates)
        }

    private fun generateDelegationCandidates(delegatedInterfaceType: KotlinType): Collection<T> =
        getDelegatableMembers(delegatedInterfaceType).map { memberDescriptor ->
            val newModality = if (memberDescriptor.modality == Modality.ABSTRACT) Modality.OPEN else memberDescriptor.modality
            @Suppress("UNCHECKED_CAST")
            memberDescriptor.newCopyBuilder()
                .setOwner(ownerDescriptor)
                .setDispatchReceiverParameter(ownerDescriptor.thisAsReceiverParameter)
                .setModality(newModality)
                .setVisibility(Visibilities.INHERITED)
                .setKind(DELEGATION)
                .setCopyOverrides(false)
                .build() as T
        }

    private fun checkClashWithOtherDelegatedMember(candidate: T, delegatedMembers: Collection<T>): Boolean {
        val alreadyDelegated = delegatedMembers.firstOrNull { isOverridableBy(it, candidate) }
        if (alreadyDelegated != null) {
            if (classOrObject is KtClassOrObject) // report errors only for physical (non-synthetic) classes or objects
                trace.report(MANY_IMPL_MEMBER_NOT_IMPLEMENTED.on(classOrObject, classOrObject, alreadyDelegated))
            return true
        }
        return false
    }


    private fun getDelegatableMembers(interfaceType: KotlinType): Collection<T> =
        memberExtractor.getMembersByType(interfaceType).filter { descriptor ->
            descriptor.isOverridable &&
                    (descriptor.kind.isReal || !descriptor.overridesClassMembersOnly()) &&
                    delegationFilter.filter(descriptor, languageVersionSettings)
        }

    private fun T.overridesClassMembersOnly() =
        DescriptorUtils.getAllOverriddenDeclarations(this).all {
            DescriptorUtils.isClass(it.containingDeclaration)
        }

    interface MemberExtractor<out T : CallableMemberDescriptor> {
        fun getMembersByType(type: KotlinType): Collection<T>
    }

    interface TypeResolver {
        fun resolve(reference: KtTypeReference): KotlinType?
    }

    companion object {
        fun <T : CallableMemberDescriptor> generateDelegatedMembers(
            classOrObject: KtPureClassOrObject,
            ownerDescriptor: ClassDescriptor,
            existingMembers: Collection<CallableDescriptor>,
            trace: BindingTrace,
            memberExtractor: MemberExtractor<T>,
            typeResolver: TypeResolver,
            delegationFilter: DelegationFilter,
            languageVersionSettings: LanguageVersionSettings
        ): Collection<T> =
            DelegationResolver(
                classOrObject,
                ownerDescriptor,
                existingMembers,
                trace,
                memberExtractor,
                typeResolver,
                delegationFilter,
                languageVersionSettings
            )
                .generateDelegatedMembers()

        private fun isOverridingAnyOf(
            candidate: CallableMemberDescriptor,
            possiblyOverriddenBy: Collection<CallableDescriptor>
        ): Boolean =
            possiblyOverriddenBy.any { isOverridableBy(it, candidate) }

        private fun isOverridableBy(memberOne: CallableDescriptor, memberTwo: CallableDescriptor): Boolean =
            OverridingUtil.DEFAULT.isOverridableBy(memberOne, memberTwo, null).result == OVERRIDABLE

        // class Foo : Bar by baz
        //   descriptor = Foo
        //   toInterface = Bar
        //   delegateExpressionType = typeof(baz)
        // return Map<member of Foo, corresponding member of typeOf(baz)>
        fun getDelegates(
            descriptor: ClassDescriptor,
            toInterface: ClassDescriptor,
            delegateExpressionType: KotlinType? = null
        ): Map<CallableMemberDescriptor, CallableMemberDescriptor> {
            if (delegateExpressionType?.isDynamic() ?: false) return emptyMap()

            val delegatedMembers = descriptor.defaultType.memberScope.getContributedDescriptors().asSequence()
                .filterIsInstance<CallableMemberDescriptor>()
                .filter { it.kind == CallableMemberDescriptor.Kind.DELEGATION }
                .asIterable()
                .sortedWith(MemberComparator.INSTANCE)

            return delegatedMembers
                .keysToMapExceptNulls { delegatingMember ->
                    val actualDelegates = DescriptorUtils.getAllOverriddenDescriptors(delegatingMember)
                        .filter { it.containingDeclaration == toInterface }
                        .map { overriddenDescriptor ->
                            val scope = (delegateExpressionType ?: toInterface.defaultType).memberScope
                            val name = overriddenDescriptor.name

                            // this is the actual member of delegateExpressionType that we are delegating to
                            (scope.getContributedFunctions(name, NoLookupLocation.WHEN_CHECK_OVERRIDES) +
                                    scope.getContributedVariables(name, NoLookupLocation.WHEN_CHECK_OVERRIDES))
                                .firstOrNull { it == overriddenDescriptor || OverridingUtil.overrides(it, overriddenDescriptor) }
                        }

                    actualDelegates.firstOrNull()
                }
        }
    }
}
