/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.deprecation.DeprecationResolver

interface DeclarationChecker {
    fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext)
}

class DeclarationCheckerContext(
    override val trace: BindingTrace,
    override val languageVersionSettings: LanguageVersionSettings,
    override val deprecationResolver: DeprecationResolver,
    override val moduleDescriptor: ModuleDescriptor,
    val expectActualTracker: ExpectActualTracker
) : CheckerContext
