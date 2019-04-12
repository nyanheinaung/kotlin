/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.utils.memberScopeAsImportingScope

fun ModuleDescriptor.builtInPackageAsLexicalScope(): LexicalScope.Base {
    val packageView = getPackage(KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME)
    return LexicalScope.Base(packageView.memberScope.memberScopeAsImportingScope(), this)
}
