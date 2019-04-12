/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("KClassesJvm")

package kotlin.reflect.jvm

import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.KClassImpl

/**
 * Returns the JVM name of the class represented by this [KClass] instance.
 *
 * @see [java.lang.Class.getName]
 */
val KClass<*>.jvmName: String
    get() = (this as KClassImpl).jClass.name
