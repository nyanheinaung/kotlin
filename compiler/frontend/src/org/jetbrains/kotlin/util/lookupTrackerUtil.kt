/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.incremental.KotlinLookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.record
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun LookupTracker.record(expression: KtExpression, type: KotlinType) {
    if (type.isError || type.isUnit()) return

    val typeDescriptor = type.constructor.declarationDescriptor ?: return
    val scopeDescriptor = typeDescriptor.containingDeclaration

    // Scope descriptor is function descriptor only when type is local
    // Lookups for local types are not needed since all usages are compiled with the type
    when {
        scopeDescriptor is PackageFragmentDescriptor && !DescriptorUtils.isLocal(typeDescriptor) -> {
            record(KotlinLookupLocation(expression), scopeDescriptor, typeDescriptor.name)
        }
        scopeDescriptor is ClassDescriptor && !DescriptorUtils.isLocal(typeDescriptor) -> {
            record(KotlinLookupLocation(expression), scopeDescriptor, typeDescriptor.name)
        }
    }

    for (typeArgument in type.arguments) {
        if (!typeArgument.isStarProjection) {
            record(expression, typeArgument.type)
        }
    }
}
