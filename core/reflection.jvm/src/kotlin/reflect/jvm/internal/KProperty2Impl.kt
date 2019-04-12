/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KMutableProperty2
import kotlin.reflect.KProperty2

internal open class KProperty2Impl<D, E, out R> : KProperty2<D, E, R>, KPropertyImpl<R> {
    constructor(container: KDeclarationContainerImpl, name: String, signature: String) : super(
        container, name, signature, CallableReference.NO_RECEIVER
    )

    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    private val _getter = ReflectProperties.lazy { Getter(this) }

    override val getter: Getter<D, E, R> get() = _getter()

    override fun get(receiver1: D, receiver2: E): R = getter.call(receiver1, receiver2)

    private val delegateField = lazy(PUBLICATION) { computeDelegateField() }

    override fun getDelegate(receiver1: D, receiver2: E): Any? = getDelegate(delegateField.value, receiver1)

    override fun invoke(receiver1: D, receiver2: E): R = get(receiver1, receiver2)

    class Getter<D, E, out R>(override val property: KProperty2Impl<D, E, R>) : KPropertyImpl.Getter<R>(), KProperty2.Getter<D, E, R> {
        override fun invoke(receiver1: D, receiver2: E): R = property.get(receiver1, receiver2)
    }
}

internal class KMutableProperty2Impl<D, E, R> : KProperty2Impl<D, E, R>, KMutableProperty2<D, E, R> {
    constructor(container: KDeclarationContainerImpl, name: String, signature: String) : super(container, name, signature)

    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    private val _setter = ReflectProperties.lazy { Setter(this) }

    override val setter: Setter<D, E, R> get() = _setter()

    override fun set(receiver1: D, receiver2: E, value: R) = setter.call(receiver1, receiver2, value)

    class Setter<D, E, R>(override val property: KMutableProperty2Impl<D, E, R>) : KPropertyImpl.Setter<R>(),
        KMutableProperty2.Setter<D, E, R> {
        override fun invoke(receiver1: D, receiver2: E, value: R): Unit = property.set(receiver1, receiver2, value)
    }
}
