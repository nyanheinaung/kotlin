/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KProperties")
package kotlin.reflect.full

import kotlin.reflect.KProperty1
import kotlin.reflect.KProperty2
import kotlin.reflect.jvm.internal.KPropertyImpl

/**
 * Returns the instance of a delegated **extension property**, or `null` if this property is not delegated.
 * Throws an exception if this is not an extension property.
 *
 * @see [KProperty1.getDelegate]
 */
@SinceKotlin("1.1")
fun KProperty1<*, *>.getExtensionDelegate(): Any? {
    @Suppress("UNCHECKED_CAST")
    return (this as KProperty1<Any?, *>).getDelegate(KPropertyImpl.EXTENSION_PROPERTY_DELEGATE)
}

/**
 * Returns the instance of a delegated **member extension property**, or `null` if this property is not delegated.
 * Throws an exception if this is not an extension property.
 *
 * @param receiver the instance of the class used to retrieve the value of the property delegate.
 *
 * @see [KProperty2.getDelegate]
 */
@SinceKotlin("1.1")
fun <D> KProperty2<D, *, *>.getExtensionDelegate(receiver: D): Any? {
    @Suppress("UNCHECKED_CAST")
    return (this as KProperty2<D, Any?, *>).getDelegate(receiver, KPropertyImpl.EXTENSION_PROPERTY_DELEGATE)
}
