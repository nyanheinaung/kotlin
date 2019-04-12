/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

interface ModuleDescriptor : DeclarationDescriptor {
    override fun getContainingDeclaration(): DeclarationDescriptor? = null

    val builtIns: KotlinBuiltIns

    /**
     * Stable name of *Kotlin* module. Can be used for ABI (e.g. for mangling of declarations)
     */
    val stableName: Name?

    fun shouldSeeInternalsOf(targetModule: ModuleDescriptor): Boolean

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R {
        return visitor.visitModuleDeclaration(this, data)
    }

    fun getPackage(fqName: FqName): PackageViewDescriptor

    fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName>

    /**
     * @return dependency modules in the same order in which this module depends on them. Does not include `this`
     */
    val allDependencyModules: List<ModuleDescriptor>

    val expectedByModules: List<ModuleDescriptor>

    fun <T> getCapability(capability: Capability<T>): T?

    class Capability<T>(val name: String) {
        override fun toString() = name
    }

    val isValid: Boolean

    fun assertValid()
}
