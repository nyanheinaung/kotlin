/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.resolve.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.js.resolve.MODULE_KIND
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.serialization.js.ModuleKind

fun checkJsModuleUsage(
        bindingContext: BindingContext,
        diagnosticSink: DiagnosticSink,
        container: DeclarationDescriptor,
        callee: DeclarationDescriptor,
        reportOn: PsiElement
) {
    val module = DescriptorUtils.getContainingModule(container)
    val moduleKind = bindingContext[MODULE_KIND, module] ?: return
    val calleeRootContainer = findRoot(callee)

    val callToModule = AnnotationsUtils.getModuleName(calleeRootContainer) != null ||
                       AnnotationsUtils.getFileModuleName(bindingContext, calleeRootContainer) != null
    val callToNonModule = AnnotationsUtils.isNonModule(calleeRootContainer) ||
                          AnnotationsUtils.isFromNonModuleFile(bindingContext, calleeRootContainer)

    if (moduleKind == ModuleKind.UMD) {
        if (!callToNonModule && callToModule || callToNonModule && !callToModule) {
            diagnosticSink.report(ErrorsJs.CALL_FROM_UMD_MUST_BE_JS_MODULE_AND_JS_NON_MODULE.on(reportOn))
        }
    }
    else {
        if (moduleKind == ModuleKind.PLAIN) {
            if (!callToNonModule && callToModule) {
                diagnosticSink.report(ErrorsJs.CALL_TO_JS_MODULE_WITHOUT_MODULE_SYSTEM.on(reportOn, normalizeDescriptor(callee)))
            }
        }
        else {
            if (!callToModule && callToNonModule) {
                diagnosticSink.report(ErrorsJs.CALL_TO_JS_NON_MODULE_WITH_MODULE_SYSTEM.on(reportOn, normalizeDescriptor(callee)))
            }
        }
    }
}

private fun normalizeDescriptor(descriptor: DeclarationDescriptor): DeclarationDescriptor {
    if (descriptor is FakeCallableDescriptorForObject) return descriptor.classDescriptor
    return descriptor
}

private fun findRoot(callee: DeclarationDescriptor) = generateSequence(callee) { it.containingDeclaration }
    .takeWhile { it !is PackageFragmentDescriptor }
    .last()