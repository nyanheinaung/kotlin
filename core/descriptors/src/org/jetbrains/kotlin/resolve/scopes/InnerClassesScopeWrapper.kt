/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptorWithTypeParameters
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.Printer

class InnerClassesScopeWrapper(val workerScope: MemberScope) : MemberScopeImpl() {
    override fun getContributedClassifier(name: Name, location: LookupLocation): ClassifierDescriptor? =
            workerScope.getContributedClassifier(name, location)?.let {
                it as? ClassDescriptor ?: it as? TypeAliasDescriptor
            }

    override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): List<ClassifierDescriptor> {
        val restrictedFilter = kindFilter.restrictedToKindsOrNull(DescriptorKindFilter.CLASSIFIERS_MASK) ?: return listOf()
        return workerScope.getContributedDescriptors(restrictedFilter, nameFilter).filterIsInstance<ClassifierDescriptorWithTypeParameters>()
    }

    override fun printScopeStructure(p: Printer) {
        p.println("InnerClassesScopeWrapper for scope:")
        workerScope.printScopeStructure(p)
    }

    override fun getFunctionNames() = workerScope.getFunctionNames()
    override fun getVariableNames() = workerScope.getVariableNames()
    override fun getClassifierNames() = workerScope.getClassifierNames()

    override fun definitelyDoesNotContainName(name: Name) = workerScope.definitelyDoesNotContainName(name)

    override fun toString() = "Classes from $workerScope"
}
