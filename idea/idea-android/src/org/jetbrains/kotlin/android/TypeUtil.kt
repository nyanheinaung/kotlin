/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType


internal fun KotlinType.fqNameSafe() = constructor.declarationDescriptor?.fqNameSafe

internal fun KotlinType.isSubclassOf(className: String, strict: Boolean = false): Boolean {
    return (!strict && fqNameSafe()?.asString() == className) || constructor.supertypes.any {
        it.fqNameSafe()?.asString() == className || it.isSubclassOf(className, true)
    }
}
