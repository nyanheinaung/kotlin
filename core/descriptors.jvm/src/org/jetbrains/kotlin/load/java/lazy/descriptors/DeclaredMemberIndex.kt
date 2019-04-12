/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy.descriptors

import org.jetbrains.kotlin.load.java.components.DescriptorResolverUtils
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.load.java.structure.JavaMember
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.name.Name

interface DeclaredMemberIndex {
    fun findMethodsByName(name: Name): Collection<JavaMethod>
    fun getMethodNames(): Set<Name>

    fun findFieldByName(name: Name): JavaField?
    fun getFieldNames(): Set<Name>

    object Empty : DeclaredMemberIndex {
        override fun findMethodsByName(name: Name) = listOf<JavaMethod>()
        override fun getMethodNames() = emptySet<Name>()

        override fun findFieldByName(name: Name): JavaField? = null
        override fun getFieldNames() = emptySet<Name>()
    }
}

open class ClassDeclaredMemberIndex(
    val jClass: JavaClass,
    private val memberFilter: (JavaMember) -> Boolean
) : DeclaredMemberIndex {
    private val methodFilter = { m: JavaMethod ->
        memberFilter(m) && !DescriptorResolverUtils.isObjectMethodInInterface(m)
    }

    private val methods = jClass.methods.asSequence().filter(methodFilter).groupBy { m -> m.name }
    private val fields = jClass.fields.asSequence().filter(memberFilter).associateBy { m -> m.name }

    override fun findMethodsByName(name: Name): Collection<JavaMethod> = methods[name] ?: listOf()
    override fun getMethodNames(): Set<Name> = jClass.methods.asSequence().filter(methodFilter).mapTo(mutableSetOf(), JavaMethod::name)

    override fun findFieldByName(name: Name): JavaField? = fields[name]
    override fun getFieldNames(): Set<Name> = jClass.fields.asSequence().filter(memberFilter).mapTo(mutableSetOf(), JavaField::name)
}

