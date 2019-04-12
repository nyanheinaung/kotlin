/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("ImportsUtils")

package org.jetbrains.kotlin.idea.imports

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getImportableDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.util.*

object ImportPathComparator : Comparator<ImportPath> {
    override fun compare(import1: ImportPath, import2: ImportPath): Int {
        // alias imports placed last
        if (import1.hasAlias() != import2.hasAlias()) {
            return if (import1.hasAlias()) +1 else -1
        }

        // standard library imports last
        val stdlib1 = isJavaOrKotlinStdlibImport(import1)
        val stdlib2 = isJavaOrKotlinStdlibImport(import2)
        if (stdlib1 != stdlib2) {
            return if (stdlib1) +1 else -1
        }

        return import1.toString().compareTo(import2.toString())
    }

    private fun isJavaOrKotlinStdlibImport(path: ImportPath): Boolean {
        val s = path.pathStr
        return s.startsWith("java.") || s.startsWith("javax.") || s.startsWith("kotlin.")
    }
}

val DeclarationDescriptor.importableFqName: FqName?
    get() {
        if (!canBeReferencedViaImport()) return null
        return getImportableDescriptor().fqNameSafe
    }

fun DeclarationDescriptor.canBeReferencedViaImport(): Boolean {
    if (this is PackageViewDescriptor ||
        DescriptorUtils.isTopLevelDeclaration(this) ||
        this is CallableDescriptor && DescriptorUtils.isStaticDeclaration(this)
    ) {
        return !name.isSpecial
    }

    //Both TypeAliasDescriptor and ClassDescriptor
    val parentClassifier = containingDeclaration as? ClassifierDescriptorWithTypeParameters ?: return false
    if (!parentClassifier.canBeReferencedViaImport()) return false

    return when (this) {
        is ConstructorDescriptor -> !parentClassifier.isInner // inner class constructors can't be referenced via import
        is ClassDescriptor, is TypeAliasDescriptor -> true
        else -> parentClassifier is ClassDescriptor && parentClassifier.kind == ClassKind.OBJECT
    }
}

fun DeclarationDescriptor.canBeAddedToImport(): Boolean = this !is PackageViewDescriptor && canBeReferencedViaImport()

fun KotlinType.canBeReferencedViaImport(): Boolean {
    val descriptor = constructor.declarationDescriptor
    return descriptor != null && descriptor.canBeReferencedViaImport()
}

// for cases when class qualifier refers companion object treats it like reference to class itself
fun KtReferenceExpression.getImportableTargets(bindingContext: BindingContext): Collection<DeclarationDescriptor> {
    val targets = bindingContext[BindingContext.SHORT_REFERENCE_TO_COMPANION_OBJECT, this]?.let { listOf(it) }
        ?: getReferenceTargets(bindingContext)
    return targets.map { it.getImportableDescriptor() }.toSet()
}

