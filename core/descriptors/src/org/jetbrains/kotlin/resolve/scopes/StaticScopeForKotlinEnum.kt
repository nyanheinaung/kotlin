/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory.createEnumValueOfMethod
import org.jetbrains.kotlin.resolve.DescriptorFactory.createEnumValuesMethod
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue
import org.jetbrains.kotlin.utils.Printer
import java.util.*

// We don't need to track lookups here since this scope used only for introduce special Enum class members
class StaticScopeForKotlinEnum(
        storageManager: StorageManager,
        private val containingClass: ClassDescriptor
) : MemberScopeImpl() {
    init {
        assert(containingClass.kind == ClassKind.ENUM_CLASS) { "Class should be an enum: $containingClass" }
    }

    override fun getContributedClassifier(name: Name, location: LookupLocation) = null // TODO

    private val functions: List<SimpleFunctionDescriptor> by storageManager.createLazyValue {
        listOf(createEnumValueOfMethod(containingClass), createEnumValuesMethod(containingClass))
    }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean) = functions

    override fun getContributedFunctions(name: Name, location: LookupLocation) =
            functions.filterTo(ArrayList<SimpleFunctionDescriptor>(1)) { it.name == name }

    override fun printScopeStructure(p: Printer) {
        p.println("Static scope for $containingClass")
    }
}
