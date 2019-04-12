/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.checkers

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.hasDynamicExtensionAnnotation
import org.jetbrains.kotlin.types.isDynamic

object DynamicReceiverChecker : DeclarationChecker {
    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is CallableDescriptor || declaration !is KtCallableDeclaration || descriptor.hasDynamicExtensionAnnotation()) return

        // function expression
        if (declaration is KtNamedFunction && declaration.name == null) return

        if (descriptor.extensionReceiverParameter?.value?.type?.isDynamic() == true) {
            context.trace.report(Errors.DYNAMIC_RECEIVER_NOT_ALLOWED.on(declaration))
        }
    }
}
