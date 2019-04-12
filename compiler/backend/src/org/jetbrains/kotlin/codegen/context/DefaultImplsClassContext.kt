/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context

import org.jetbrains.kotlin.codegen.AccessorForCallableDescriptor
import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

class DefaultImplsClassContext(
    typeMapper: KotlinTypeMapper,
    contextDescriptor: ClassDescriptor,
    contextKind: OwnerKind,
    parentContext: CodegenContext<*>?,
    localLookup: ((DeclarationDescriptor) -> Boolean)?,
    val interfaceContext: ClassContext
) : ClassContext(typeMapper, contextDescriptor, contextKind, parentContext, localLookup) {

    override fun getCompanionObjectContext(): CodegenContext<*>? = interfaceContext.companionObjectContext

    override fun getAccessors(): Collection<AccessorForCallableDescriptor<*>> {
        val accessors = super.getAccessors()
        val alreadyExistKeys = accessors.map({ Pair(it.calleeDescriptor, it.superCallTarget) })
        val filtered = interfaceContext.accessors.associateByTo(linkedMapOf()) { Pair(it.calleeDescriptor, it.superCallTarget) }
            .apply { keys -= alreadyExistKeys }
        return accessors + filtered.values
    }
}