/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.Name
import kotlin.reflect.KCallable

internal object EmptyContainerForLocal : KDeclarationContainerImpl() {
    override val jClass: Class<*>
        get() = fail()

    override val members: Collection<KCallable<*>>
        get() = fail()

    override val constructorDescriptors: Collection<ConstructorDescriptor>
        get() = fail()

    override fun getProperties(name: Name): Collection<PropertyDescriptor> = fail()

    override fun getFunctions(name: Name): Collection<FunctionDescriptor> = fail()

    override fun getLocalProperty(index: Int): PropertyDescriptor? = null

    private fun fail(): Nothing = throw KotlinReflectionInternalError(
        "Introspecting local functions, lambdas, anonymous functions and local variables is not yet fully supported in Kotlin reflection"
    )
}
