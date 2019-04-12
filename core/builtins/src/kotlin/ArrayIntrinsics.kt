/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin

import kotlin.internal.PureReifiable

/**
 * Returns an empty array of the specified type [T].
 */
public inline fun <reified @PureReifiable T> emptyArray(): Array<T> =
        @Suppress("UNCHECKED_CAST")
        (arrayOfNulls<T>(0) as Array<T>)
