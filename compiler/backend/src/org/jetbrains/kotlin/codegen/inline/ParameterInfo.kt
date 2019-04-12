/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.org.objectweb.asm.Type

open class ParameterInfo(
    val type: Type,
    val isSkipped: Boolean, //for skipped parameter: e.g. inlined lambda
    val index: Int,
    var remapValue: StackValue?, //in case when parameter could be extracted from outer context (e.g. from local var)
    val declarationIndex: Int
) {

    var isCaptured: Boolean = false
    var functionalArgument: FunctionalArgument? = null

    val isSkippedOrRemapped: Boolean
        get() = isSkipped || isRemapped

    val isRemapped: Boolean
        get() = remapValue != null

    constructor(type: Type, skipped: Boolean, index: Int, remapValue: Int, declarationIndex: Int) : this(
        type,
        skipped,
        index,
        if (remapValue == -1) null else StackValue.local(remapValue, type),
        declarationIndex
    )
}
