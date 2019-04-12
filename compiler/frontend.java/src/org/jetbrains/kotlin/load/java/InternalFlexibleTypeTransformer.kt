/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.TypeResolver.TypeTransformerForTests
import org.jetbrains.kotlin.types.*

object InternalFlexibleTypeTransformer : TypeTransformerForTests() {
    // This is a "magic" classifier: when type resolver sees it in the code, e.g. ft<Foo, Foo?>, instead of creating a normal type,
    // it creates a flexible type, e.g. (Foo..Foo?).
    // This is used in tests and Evaluate Expression to have flexible types in the code,
    // but normal users should not be referencing this classifier
    @JvmField
    val FLEXIBLE_TYPE_CLASSIFIER: ClassId = ClassId.topLevel(FqName("kotlin.internal.flexible.ft"))

    override fun transformType(kotlinType: KotlinType): KotlinType? {
        val descriptor = kotlinType.constructor.declarationDescriptor
        if (descriptor != null && FLEXIBLE_TYPE_CLASSIFIER.asSingleFqName().toUnsafe() == DescriptorUtils.getFqName(descriptor)
            && kotlinType.arguments.size == 2) {
            return KotlinTypeFactory.flexibleType(kotlinType.arguments[0].type.unwrap() as SimpleType, kotlinType.arguments[1].type.unwrap() as SimpleType)
        }
        return null
    }
}
