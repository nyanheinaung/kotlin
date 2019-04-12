/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.AnnotationResolver
import org.jetbrains.kotlin.resolve.AnnotationResolverImpl
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil
import org.jetbrains.kotlin.resolve.lazy.LazyEntity
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.source.toSourceElement
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

abstract class LazyAnnotationsContext(
    val annotationResolver: AnnotationResolver,
    val storageManager: StorageManager,
    val trace: BindingTrace
) {
    abstract val scope: LexicalScope
}

class LazyAnnotationsContextImpl(
    annotationResolver: AnnotationResolver,
    storageManager: StorageManager,
    trace: BindingTrace,
    override val scope: LexicalScope
) : LazyAnnotationsContext(annotationResolver, storageManager, trace)

class LazyAnnotations(
    val c: LazyAnnotationsContext,
    val annotationEntries: List<KtAnnotationEntry>
) : Annotations, LazyEntity {
    override fun isEmpty() = annotationEntries.isEmpty()

    private val annotation = c.storageManager.createMemoizedFunction { entry: KtAnnotationEntry ->
        LazyAnnotationDescriptor(c, entry)
    }

    override fun iterator(): Iterator<AnnotationDescriptor> = annotationEntries.asSequence().map(annotation).iterator()

    override fun forceResolveAllContents() {
        // To resolve all entries
        for (annotation in this) {
            // TODO: probably we should do ForceResolveUtil.forceResolveAllContents(annotation) here
        }
    }
}

class LazyAnnotationDescriptor(
    val c: LazyAnnotationsContext,
    val annotationEntry: KtAnnotationEntry
) : AnnotationDescriptor, LazyEntity {

    init {
        c.trace.record(BindingContext.ANNOTATION, annotationEntry, this)
    }

    override val type by c.storageManager.createLazyValue {
        c.annotationResolver.resolveAnnotationType(scope, annotationEntry, c.trace)
    }

    override val source = annotationEntry.toSourceElement()

    private val scope = if (c.scope.ownerDescriptor is PackageFragmentDescriptor) {
        LexicalScope.Base(c.scope, FileDescriptorForVisibilityChecks(source, c.scope.ownerDescriptor))
    } else {
        c.scope
    }

    override val allValueArguments by c.storageManager.createLazyValue {
        val resolutionResults = c.annotationResolver.resolveAnnotationCall(annotationEntry, scope, c.trace)
        AnnotationResolverImpl.checkAnnotationType(annotationEntry, c.trace, resolutionResults)

        if (!resolutionResults.isSingleResult) return@createLazyValue emptyMap<Name, ConstantValue<*>>()

        resolutionResults.resultingCall.valueArguments.mapNotNull { (valueParameter, resolvedArgument) ->
            if (resolvedArgument == null) null
            else c.annotationResolver.getAnnotationArgumentValue(c.trace, valueParameter, resolvedArgument)?.let { value ->
                valueParameter.name to value
            }
        }.toMap()
    }

    override fun forceResolveAllContents() {
        ForceResolveUtil.forceResolveAllContents(type)
        allValueArguments
    }

    private class FileDescriptorForVisibilityChecks(
        private val source: SourceElement,
        private val containingDeclaration: DeclarationDescriptor
    ) : DeclarationDescriptorWithSource {
        override val annotations: Annotations get() = Annotations.EMPTY
        override fun getContainingDeclaration() = containingDeclaration
        override fun getSource() = source
        override fun getOriginal() = this
        override fun getName() = Name.special("< file descriptor for annotation resolution >")

        private fun error(): Nothing = error("This method should not be called")
        override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>?, data: D): R = error()
        override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>?) = error()

        override fun toString(): String = "${name.asString()} declared in LazyAnnotations.kt"
    }
}
