/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

internal open class KProperty1Impl<T, out R> : KProperty1<T, R>, KPropertyImpl<R> {
    constructor(container: KDeclarationContainerImpl, name: String, signature: String, boundReceiver: Any?) : super(
        container, name, signature, boundReceiver
    )

    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    private val _getter = ReflectProperties.lazy { Getter(this) }

    override val getter: Getter<T, R> get() = _getter()

    override fun get(receiver: T): R = getter.call(receiver)

    private val delegateField = lazy(PUBLICATION) { computeDelegateField() }

    override fun getDelegate(receiver: T): Any? = getDelegate(delegateField.value, receiver)

    override fun invoke(receiver: T): R = get(receiver)

    class Getter<T, out R>(override val property: KProperty1Impl<T, R>) : KPropertyImpl.Getter<R>(), KProperty1.Getter<T, R> {
        override fun invoke(receiver: T): R = property.get(receiver)
    }
}

internal class KMutableProperty1Impl<T, R> : KProperty1Impl<T, R>, KMutableProperty1<T, R> {
    constructor(container: KDeclarationContainerImpl, name: String, signature: String, boundReceiver: Any?) : super(
        container, name, signature, boundReceiver
    )

    constructor(container: KDeclarationContainerImpl, descriptor: PropertyDescriptor) : super(container, descriptor)

    private val _setter = ReflectProperties.lazy { Setter(this) }

    override val setter: Setter<T, R> get() = _setter()

    override fun set(receiver: T, value: R) = setter.call(receiver, value)

    class Setter<T, R>(override val property: KMutableProperty1Impl<T, R>) : KPropertyImpl.Setter<R>(), KMutableProperty1.Setter<T, R> {
        override fun invoke(receiver: T, value: R): Unit = property.set(receiver, value)
    }
}
