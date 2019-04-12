/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.components

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.load.java.components.AbstractJavaResolverCache
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.resolve.lazy.ResolveSession

class StubJavaResolverCache(resolveSession: ResolveSession) : AbstractJavaResolverCache(resolveSession) {

    override fun recordMethod(method: JavaMethod, descriptor: SimpleFunctionDescriptor) {}

    override fun recordConstructor(element: JavaElement, descriptor: ConstructorDescriptor) {}

    override fun recordField(field: JavaField, descriptor: PropertyDescriptor) {}

    override fun recordClass(javaClass: JavaClass, descriptor: ClassDescriptor) {}

}