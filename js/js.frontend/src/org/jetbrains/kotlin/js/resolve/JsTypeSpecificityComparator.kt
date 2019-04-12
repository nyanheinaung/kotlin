/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve

import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.types.isFlexible

object JsTypeSpecificityComparator: TypeSpecificityComparator {

    private fun checkOnlyDynamicFlexibleType(type: KotlinType) {
        if (type.isFlexible()) {
            assert(type.isDynamic()) {
                "Unexpected flexible type in Js: $type"
            }
        }
    }

    override fun isDefinitelyLessSpecific(specific: KotlinType, general: KotlinType): Boolean {
        checkOnlyDynamicFlexibleType(specific)
        checkOnlyDynamicFlexibleType(general)

        return specific.isDynamic() && !general.isDynamic()
    }
}