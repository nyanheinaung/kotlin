/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.extensions

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.js.translate.declaration.DeclarationBodyVisitor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlinx.serialization.compiler.backend.js.SerializableCompanionJsTranslator
import org.jetbrains.kotlinx.serialization.compiler.backend.js.SerializableJsTranslator
import org.jetbrains.kotlinx.serialization.compiler.backend.js.SerializerJsTranslator

open class SerializationJsExtension: JsSyntheticTranslateExtension {
    override fun generateClassSyntheticParts(declaration: KtPureClassOrObject, descriptor: ClassDescriptor, translator: DeclarationBodyVisitor, context: TranslationContext) {
        SerializerJsTranslator.translate(declaration, descriptor, translator, context)
        SerializableJsTranslator.translate(declaration, descriptor, translator, context)
        SerializableCompanionJsTranslator.translate(declaration, descriptor, translator, context)
    }
}