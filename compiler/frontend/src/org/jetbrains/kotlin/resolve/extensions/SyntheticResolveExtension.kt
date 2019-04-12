/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.extensions

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult
import java.util.*

//----------------------------------------------------------------
// extension interface

interface SyntheticResolveExtension {
    companion object : ProjectExtensionDescriptor<SyntheticResolveExtension>(
        "org.jetbrains.kotlin.syntheticResolveExtension", SyntheticResolveExtension::class.java
    ) {
        fun getInstance(project: Project): SyntheticResolveExtension {
            val instances = getInstances(project)
            if (instances.size == 1) return instances.single()
            // return list combiner here
            return object : SyntheticResolveExtension {
                override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
                    instances.flatMap { withLinkageErrorLogger(it) { getSyntheticNestedClassNames(thisDescriptor) } }

                override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
                    instances.flatMap { withLinkageErrorLogger(it) { getSyntheticFunctionNames(thisDescriptor) } }

                override fun generateSyntheticClasses(
                    thisDescriptor: ClassDescriptor, name: Name,
                    ctx: LazyClassContext, declarationProvider: ClassMemberDeclarationProvider,
                    result: MutableSet<ClassDescriptor>
                ) =
                    instances.forEach {
                        withLinkageErrorLogger(it) {
                            generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
                        }
                    }

                override fun generateSyntheticClasses(
                    thisDescriptor: PackageFragmentDescriptor, name: Name,
                    ctx: LazyClassContext, declarationProvider: PackageMemberDeclarationProvider,
                    result: MutableSet<ClassDescriptor>
                ) =
                    instances.forEach {
                        withLinkageErrorLogger(it) {
                            generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
                        }
                    }

                override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
                    instances.firstNotNullResult { withLinkageErrorLogger(it) { getSyntheticCompanionObjectNameIfNeeded(thisDescriptor) } }

                override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) =
                    instances.forEach { withLinkageErrorLogger(it) { addSyntheticSupertypes(thisDescriptor, supertypes) } }

                // todo revert
                override fun generateSyntheticMethods(
                    thisDescriptor: ClassDescriptor, name: Name,
                    bindingContext: BindingContext,
                    fromSupertypes: List<SimpleFunctionDescriptor>,
                    result: MutableCollection<SimpleFunctionDescriptor>
                ) =
                    instances.forEach {
                        withLinkageErrorLogger(it) {
                            generateSyntheticMethods(
                                thisDescriptor,
                                name,
                                bindingContext,
                                fromSupertypes,
                                result
                            )
                        }
                    }

                override fun generateSyntheticProperties(
                    thisDescriptor: ClassDescriptor, name: Name,
                    bindingContext: BindingContext,
                    fromSupertypes: ArrayList<PropertyDescriptor>,
                    result: MutableSet<PropertyDescriptor>
                ) =
                    instances.forEach {
                        withLinkageErrorLogger(it) {
                            generateSyntheticProperties(
                                thisDescriptor,
                                name,
                                bindingContext,
                                fromSupertypes,
                                result
                            )
                        }
                    }
            }
        }
    }

    fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? = null

    fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> = emptyList()

    fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> = emptyList()

    fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {}

    fun generateSyntheticClasses(
        thisDescriptor: ClassDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: ClassMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
    ) {
    }

    fun generateSyntheticClasses(
        thisDescriptor: PackageFragmentDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: PackageMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
    ) {
    }

    fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
    }

    fun generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
    ) {
    }
}
