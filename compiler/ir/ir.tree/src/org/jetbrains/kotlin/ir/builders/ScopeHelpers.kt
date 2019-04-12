/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.builders

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.MemberDescriptor

inline fun <reified T> Scope.assertCastOwner() =
    scopeOwner as? T ?: throw AssertionError("Unexpected scopeOwner: $scopeOwner")

fun Scope.functionOwner(): FunctionDescriptor =
    assertCastOwner()

fun Scope.classOwner(): ClassDescriptor =
    scopeOwner.let {
        when (it) {
            is ClassDescriptor -> it
            is MemberDescriptor -> it.containingDeclaration as ClassDescriptor
            else -> throw AssertionError("Unexpected scopeOwner: $scopeOwner")
        }
    }
