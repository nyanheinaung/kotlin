/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

/**
 * Signals that Kotlin reflection had reached an inconsistent state from which it cannot recover.
 * @suppress
 */
class KotlinReflectionInternalError(message: String) : Error(message)
