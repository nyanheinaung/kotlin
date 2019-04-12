/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.RequireKotlinNames
import org.jetbrains.kotlin.resolve.SINCE_KOTLIN_FQ_NAME
import org.jetbrains.kotlin.resolve.source.getPsi

abstract class KotlinVersionStringAnnotationValueChecker(
    private val annotationFqName: FqName
) : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        val annotation = descriptor.annotations.findAnnotation(annotationFqName) ?: return
        val version = annotation.allValueArguments.values.singleOrNull()?.value as? String ?: return
        if (!version.matches(VERSION_REGEX)) {
            context.trace.report(Errors.ILLEGAL_KOTLIN_VERSION_STRING_VALUE.on(annotation.source.getPsi() ?: declaration, annotationFqName))
            return
        }

        extraCheck(declaration, annotation, version, context.trace, context.languageVersionSettings)
    }

    open fun extraCheck(
        declaration: KtDeclaration,
        annotation: AnnotationDescriptor,
        version: String,
        diagnosticHolder: DiagnosticSink,
        languageVersionSettings: LanguageVersionSettings
    ) {
    }

    companion object {
        val VERSION_REGEX: Regex = "(0|[1-9][0-9]*)".let { number -> Regex("$number\\.$number(\\.$number)?") }
    }
}

object SinceKotlinAnnotationValueChecker : KotlinVersionStringAnnotationValueChecker(SINCE_KOTLIN_FQ_NAME) {
    override fun extraCheck(
        declaration: KtDeclaration,
        annotation: AnnotationDescriptor,
        version: String,
        diagnosticHolder: DiagnosticSink,
        languageVersionSettings: LanguageVersionSettings
    ) {
        val apiVersion = ApiVersion.parse(version)
        val specified = languageVersionSettings.apiVersion
        if (apiVersion != null && apiVersion > specified) {
            diagnosticHolder.report(
                Errors.NEWER_VERSION_IN_SINCE_KOTLIN.on(
                    annotation.source.getPsi() ?: declaration,
                    specified.versionString
                )
            )
        }
    }
}

object RequireKotlinAnnotationValueChecker : KotlinVersionStringAnnotationValueChecker(RequireKotlinNames.FQ_NAME)
