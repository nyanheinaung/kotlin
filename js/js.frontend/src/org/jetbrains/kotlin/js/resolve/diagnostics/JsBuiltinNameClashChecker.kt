/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.naming.NameSuggestion
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker

class JsBuiltinNameClashChecker(private val nameSuggestion: NameSuggestion) : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (AnnotationsUtils.isNativeObject(descriptor)) return
        if (descriptor.containingDeclaration !is ClassDescriptor) return

        val suggestedName = nameSuggestion.suggest(descriptor)!!
        if (!suggestedName.stable) return
        val simpleName = suggestedName.names.single()

        if (descriptor is ClassDescriptor) {
            if (simpleName in PROHIBITED_STATIC_NAMES) {
                context.trace.report(ErrorsJs.JS_BUILTIN_NAME_CLASH.on(declaration, "Function.$simpleName"))
            }
        }
        else if (descriptor is CallableMemberDescriptor) {
            if (simpleName in PROHIBITED_MEMBER_NAMES) {
                context.trace.report(ErrorsJs.JS_BUILTIN_NAME_CLASH.on(declaration, "Object.prototype.$simpleName"))
            }
        }
    }

    companion object {
        @JvmField
        val PROHIBITED_STATIC_NAMES = setOf("prototype", "length", "\$metadata\$")

        @JvmField
        val PROHIBITED_MEMBER_NAMES = setOf("constructor")
    }
}
