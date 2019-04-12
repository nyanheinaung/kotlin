/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model


sealed class ResolvedCallArgument {
    abstract val arguments: List<KotlinCallArgument>

    object DefaultArgument : ResolvedCallArgument() {
        override val arguments: List<KotlinCallArgument>
            get() = emptyList()

    }

    class SimpleArgument(val callArgument: KotlinCallArgument) : ResolvedCallArgument() {
        override val arguments: List<KotlinCallArgument>
            get() = listOf(callArgument)

    }

    class VarargArgument(override val arguments: List<KotlinCallArgument>) : ResolvedCallArgument()
}