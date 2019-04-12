/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.load.java.structure.impl.JavaClassImpl
import org.jetbrains.kotlin.load.java.structure.impl.JavaElementImpl
import org.jetbrains.kotlin.load.java.structure.impl.JavaFieldImpl
import org.jetbrains.kotlin.load.java.structure.impl.JavaMethodImpl
import org.jetbrains.kotlin.resolve.BindingContext.*
import org.jetbrains.kotlin.resolve.BindingContextUtils
import org.jetbrains.kotlin.resolve.lazy.ResolveSession

class LazyResolveBasedCache(resolveSession: ResolveSession) : AbstractJavaResolverCache(resolveSession) {

    override fun recordMethod(method: JavaMethod, descriptor: SimpleFunctionDescriptor) {
        BindingContextUtils.recordFunctionDeclarationToDescriptor(trace, (method as? JavaMethodImpl)?.psi ?: return, descriptor)
    }

    override fun recordConstructor(element: JavaElement, descriptor: ConstructorDescriptor) {
        trace.record(CONSTRUCTOR, (element as? JavaElementImpl<*>)?.psi ?: return, descriptor)
    }

    override fun recordField(field: JavaField, descriptor: PropertyDescriptor) {
        trace.record(VARIABLE, (field as? JavaFieldImpl)?.psi ?: return, descriptor)
    }

    override fun recordClass(javaClass: JavaClass, descriptor: ClassDescriptor) {
        trace.record(CLASS, (javaClass as? JavaClassImpl)?.psi ?: return, descriptor)
    }

}
