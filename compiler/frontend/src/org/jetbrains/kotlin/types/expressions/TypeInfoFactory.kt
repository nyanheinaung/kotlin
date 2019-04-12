/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions.typeInfoFactory

import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

/*
 * Functions in this file are intended to create type info instances in different circumstances
 */

fun createTypeInfo(type: KotlinType?, dataFlowInfo: DataFlowInfo): KotlinTypeInfo = KotlinTypeInfo(type, dataFlowInfo)

fun createTypeInfo(type: KotlinType?, dataFlowInfo: DataFlowInfo, jumpPossible: Boolean, jumpFlowInfo: DataFlowInfo): KotlinTypeInfo =
    KotlinTypeInfo(type, dataFlowInfo, jumpPossible, jumpFlowInfo)

fun createTypeInfo(type: KotlinType?): KotlinTypeInfo = createTypeInfo(type, DataFlowInfo.EMPTY)

fun createTypeInfo(type: KotlinType?, context: ResolutionContext<*>): KotlinTypeInfo = createTypeInfo(type, context.dataFlowInfo)

fun noTypeInfo(dataFlowInfo: DataFlowInfo): KotlinTypeInfo = createTypeInfo(null, dataFlowInfo)

fun noTypeInfo(context: ResolutionContext<*>): KotlinTypeInfo = noTypeInfo(context.dataFlowInfo)