/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

// Needed for Java interop: otherwise you need to specify all the optional parameters to join, i.e. prefix, postfix, limit, truncated
fun join(collection: Iterable<Any>, separator: String) = collection.joinToString(separator)
