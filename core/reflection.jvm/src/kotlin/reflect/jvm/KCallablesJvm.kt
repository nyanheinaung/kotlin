/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KCallablesJvm")

package kotlin.reflect.jvm

import java.lang.reflect.AccessibleObject
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.internal.asKCallableImpl

/**
 * Provides a way to suppress JVM access checks for a callable.
 *
 * @getter returns `true` if JVM access checks are suppressed for this callable object.
 *         For a property, that means that all its accessors (getter, and setter for `var` properties) are accessible.
 *
 * @setter if set to `true`, suppresses JVM access checks for this callable object.
 *         For a property, both accessors are made accessible.
 *
 * @see [java.lang.reflect.AccessibleObject]
 */
var KCallable<*>.isAccessible: Boolean
    get() {
        return when (this) {
            is KMutableProperty ->
                javaField?.isAccessible ?: true &&
                        javaGetter?.isAccessible ?: true &&
                        javaSetter?.isAccessible ?: true
            is KProperty ->
                javaField?.isAccessible ?: true &&
                        javaGetter?.isAccessible ?: true
            is KProperty.Getter ->
                property.javaField?.isAccessible ?: true &&
                        javaMethod?.isAccessible ?: true
            is KMutableProperty.Setter<*> ->
                property.javaField?.isAccessible ?: true &&
                        javaMethod?.isAccessible ?: true
            is KFunction ->
                javaMethod?.isAccessible ?: true &&
                        (this.asKCallableImpl()?.defaultCaller?.member as? AccessibleObject)?.isAccessible ?: true &&
                        this.javaConstructor?.isAccessible ?: true
            else -> throw UnsupportedOperationException("Unknown callable: $this ($javaClass)")
        }
    }
    set(value) {
        when (this) {
            is KMutableProperty -> {
                javaField?.isAccessible = value
                javaGetter?.isAccessible = value
                javaSetter?.isAccessible = value
            }
            is KProperty -> {
                javaField?.isAccessible = value
                javaGetter?.isAccessible = value
            }
            is KProperty.Getter -> {
                property.javaField?.isAccessible = value
                javaMethod?.isAccessible = value
            }
            is KMutableProperty.Setter<*> -> {
                property.javaField?.isAccessible = value
                javaMethod?.isAccessible = value
            }
            is KFunction -> {
                javaMethod?.isAccessible = value
                (this.asKCallableImpl()?.defaultCaller?.member as? AccessibleObject)?.isAccessible = true
                this.javaConstructor?.isAccessible = value
            }
            else -> throw UnsupportedOperationException("Unknown callable: $this ($javaClass)")
        }
    }
