/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import org.jetbrains.kotlin.descriptors.annotations.KotlinTarget
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.AdditionalAnnotationChecker
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace

object JsQualifierChecker : AdditionalAnnotationChecker {
    override fun checkEntries(entries: List<KtAnnotationEntry>, actualTargets: List<KotlinTarget>, trace: BindingTrace) {
        val bindingContext = trace.bindingContext
        for (entry in entries) {
            val annotation = bindingContext[BindingContext.ANNOTATION, entry] ?: continue
            if (annotation.fqName != AnnotationsUtils.JS_QUALIFIER_ANNOTATION) continue
            val argument = annotation.allValueArguments.values.singleOrNull()?.value as? String ?: continue
            if (!validateQualifier(argument)) {
                val argumentPsi = entry.valueArgumentList!!.arguments[0]
                trace.report(ErrorsJs.WRONG_JS_QUALIFIER.on(argumentPsi))
            }
        }
    }

    private fun validateQualifier(qualifier: String): Boolean {
        val parts = qualifier.split('.')
        if (parts.isEmpty()) return false

        return parts.all { part ->
            part.isNotEmpty() && part[0].isJavaIdentifierStart() && part.drop(1).all(Char::isJavaIdentifierPart)
        }
    }
}
