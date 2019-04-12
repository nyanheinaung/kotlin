/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.resolve.calls.results.OverloadResolutionResults
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.types.KotlinType

abstract class AnnotationResolver {
    fun resolveAnnotationsWithoutArguments(
        scope: LexicalScope,
        modifierList: KtModifierList?,
        trace: BindingTrace
    ): Annotations = resolveAnnotationsFromModifierList(scope, modifierList, trace, false)

    fun resolveAnnotationsWithArguments(
        scope: LexicalScope,
        modifierList: KtModifierList?,
        trace: BindingTrace
    ): Annotations = resolveAnnotationsFromModifierList(scope, modifierList, trace, true)


    private fun resolveAnnotationsFromModifierList(
        scope: LexicalScope,
        modifierList: KtModifierList?,
        trace: BindingTrace,
        shouldResolveArguments: Boolean
    ): Annotations {
        if (modifierList == null) {
            return Annotations.EMPTY
        }

        return resolveAnnotationEntries(scope, modifierList.annotationEntries, trace, shouldResolveArguments)
    }

    fun resolveAnnotationsWithoutArguments(
        scope: LexicalScope,
        annotationEntries: @JvmSuppressWildcards List<KtAnnotationEntry>,
        trace: BindingTrace
    ): Annotations = resolveAnnotationEntries(scope, annotationEntries, trace, false)

    fun resolveAnnotationsWithArguments(
        scope: LexicalScope,
        annotationEntries: @JvmSuppressWildcards List<KtAnnotationEntry>,
        trace: BindingTrace
    ): Annotations = resolveAnnotationEntries(scope, annotationEntries, trace, true)

    protected abstract fun resolveAnnotationEntries(
        scope: LexicalScope,
        annotationEntries: @JvmSuppressWildcards List<KtAnnotationEntry>,
        trace: BindingTrace,
        shouldResolveArguments: Boolean
    ): Annotations


    abstract fun resolveAnnotationType(scope: LexicalScope, entryElement: KtAnnotationEntry, trace: BindingTrace): KotlinType

    abstract fun resolveAnnotationCall(
        annotationEntry: KtAnnotationEntry,
        scope: LexicalScope,
        trace: BindingTrace
    ): OverloadResolutionResults<FunctionDescriptor>

    abstract fun getAnnotationArgumentValue(
        trace: BindingTrace,
        valueParameter: ValueParameterDescriptor,
        resolvedArgument: ResolvedValueArgument
    ): ConstantValue<*>?
}
