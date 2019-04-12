/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.utils

import java.lang.reflect.InvocationTargetException

internal inline fun <T> checkedReflection(block: () -> T, onReflectionException: (Exception) -> T): T {
    return try {
        block()
    } catch (e: InvocationTargetException) {
        throw e.targetException
    } catch (e: ReflectiveOperationException) {
        onReflectionException(e)
    } catch (e: IllegalArgumentException) {
        onReflectionException(e)
    }
}