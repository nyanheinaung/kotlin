/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.jetbrains.kotlin.util.slicedMap.BasicWritableSlice
import org.jetbrains.kotlin.util.slicedMap.RewritePolicy

@JvmField
val MODULE_KIND = BasicWritableSlice<ModuleDescriptor, ModuleKind>(RewritePolicy.DO_NOTHING).apply { setDebugName("MODULE_KIND") }

@JvmField
val LEXICAL_SCOPE_FOR_JS =
    BasicWritableSlice<ResolvedCall<out FunctionDescriptor>, LexicalScope>(RewritePolicy.DO_NOTHING).apply {
        setDebugName("LEXICAL_SCOPE_FOR_JS")
    }
