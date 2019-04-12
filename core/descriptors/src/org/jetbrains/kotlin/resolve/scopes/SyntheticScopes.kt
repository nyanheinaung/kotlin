/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType


interface SyntheticScope {
    fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor>
    fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
    fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
    fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor>

    fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor>
    fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor>
    fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor>
    fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor>

    fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor?

    open class Default : SyntheticScope {
        override fun getSyntheticExtensionProperties(
            receiverTypes: Collection<KotlinType>,
            name: Name,
            location: LookupLocation
        ): Collection<PropertyDescriptor> {
            return emptyList()
        }

        override fun getSyntheticMemberFunctions(
            receiverTypes: Collection<KotlinType>,
            name: Name,
            location: LookupLocation
        ): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticStaticFunctions(
            scope: ResolutionScope,
            name: Name,
            location: LookupLocation
        ): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticConstructors(
            scope: ResolutionScope,
            name: Name,
            location: LookupLocation
        ): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticExtensionProperties(
            receiverTypes: Collection<KotlinType>,
            location: LookupLocation
        ): Collection<PropertyDescriptor> {
            return emptyList()
        }

        override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> {
            return emptyList()
        }

        override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? {
            return null
        }
    }
}

interface SyntheticScopes {
    val scopes: Collection<SyntheticScope>

    object Empty : SyntheticScopes {
        override val scopes: Collection<SyntheticScope> = emptyList()
    }
}


fun SyntheticScopes.collectSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticExtensionProperties(receiverTypes, name, location) }

fun SyntheticScopes.collectSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticMemberFunctions(receiverTypes, name, location) }

fun SyntheticScopes.collectSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticStaticFunctions(scope, name, location) }

fun SyntheticScopes.collectSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticConstructors(scope, name, location) }

fun SyntheticScopes.collectSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation)
        = scopes.flatMap { it.getSyntheticExtensionProperties(receiverTypes, location) }

fun SyntheticScopes.collectSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>)
        = scopes.flatMap { it.getSyntheticMemberFunctions(receiverTypes) }

fun SyntheticScopes.collectSyntheticStaticFunctions(scope: ResolutionScope)
        = scopes.flatMap { it.getSyntheticStaticFunctions(scope) }

fun SyntheticScopes.collectSyntheticConstructors(scope: ResolutionScope)
        = scopes.flatMap { it.getSyntheticConstructors(scope) }

fun SyntheticScopes.collectSyntheticConstructors(constructor: ConstructorDescriptor)
        = scopes.mapNotNull { it.getSyntheticConstructor(constructor) }