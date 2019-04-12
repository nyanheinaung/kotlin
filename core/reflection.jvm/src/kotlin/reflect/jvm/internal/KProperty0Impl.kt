/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

internal open class KProperty0Impl<out R> : KProperty0<R>, KPropertyImpl<R> {
    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    constructor(container: KDeclarationContainerImpl, name: String, signature: String, boundReceiver: Any?) : super(
        container, name, signature, boundReceiver
    )

    private val _getter = ReflectProperties.lazy { Getter(this) }

    override val getter: Getter<R> get() = _getter()

    override fun get(): R = getter.call()

    private val delegateFieldValue = lazy(PUBLICATION) { getDelegate(computeDelegateField(), boundReceiver) }

    override fun getDelegate(): Any? = delegateFieldValue.value

    override fun invoke(): R = get()

    class Getter<out R>(override val property: KProperty0Impl<R>) : KPropertyImpl.Getter<R>(), KProperty0.Getter<R> {
        override fun invoke(): R = property.get()
    }
}

internal class KMutableProperty0Impl<R> : KProperty0Impl<R>, KMutableProperty0<R> {
    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    constructor(container: KDeclarationContainerImpl, name: String, signature: String, boundReceiver: Any?) : super(
        container, name, signature, boundReceiver
    )

    private val _setter = ReflectProperties.lazy { Setter(this) }

    override val setter: Setter<R> get() = _setter()

    override fun set(value: R) = setter.call(value)

    class Setter<R>(override val property: KMutableProperty0Impl<R>) : KPropertyImpl.Setter<R>(), KMutableProperty0.Setter<R> {
        override fun invoke(value: R): Unit = property.set(value)
    }
}
