/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.android.extensions

import kotlinx.android.extensions.CacheImplementation.*

/**
 * Instructs Android Extensions to apply specific options for the annotated layout container.
 */
public annotation class ContainerOptions(
        /** A cache implementation for the container. */
        public val cache: CacheImplementation = HASH_MAP
)