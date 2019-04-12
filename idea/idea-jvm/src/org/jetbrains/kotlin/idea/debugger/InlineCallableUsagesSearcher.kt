/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger

import com.intellij.debugger.engine.DebugProcess
import com.intellij.openapi.application.ex.ApplicationManagerEx
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.MessageType
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.xdebugger.impl.XDebugSessionImpl
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.debugger.DebuggerClassNameProvider.Companion.getRelevantElement
import org.jetbrains.kotlin.idea.debugger.evaluate.KotlinDebuggerCaches
import org.jetbrains.kotlin.idea.debugger.evaluate.KotlinDebuggerCaches.ComputedClassNames
import org.jetbrains.kotlin.idea.search.usagesSearch.isImportUsage
import org.jetbrains.kotlin.idea.stubindex.KotlinSourceFilterScope
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.idea.util.application.runReadAction
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.isAncestor
import org.jetbrains.kotlin.psi.psiUtil.isInsideOf
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.inline.InlineUtil

class InlineCallableUsagesSearcher(private val myDebugProcess: DebugProcess) {
    fun findInlinedCalls(
            declaration: KtDeclaration,
            bindingContext: BindingContext = KotlinDebuggerCaches.getOrCreateTypeMapper(declaration).bindingContext,
            transformer: (PsiElement) -> ComputedClassNames
    ): ComputedClassNames {
        if (!checkIfInline(declaration, bindingContext)) {
            return ComputedClassNames.EMPTY
        }
        else {
            val searchResult = hashSetOf<PsiElement>()
            val declarationName = runReadAction { declaration.name }

            val task = Runnable {
                ReferencesSearch.search(declaration, getScopeForInlineDeclarationUsages(declaration)).forEach {
                    if (!runReadAction { it.isImportUsage() }) {
                        val usage = (it.element as? KtElement)?.let(::getRelevantElement)
                        if (usage != null && !runReadAction { declaration.isAncestor(usage) }) {
                            searchResult.add(usage)
                        }
                    }
                }
            }

            var isSuccess = true
            val applicationEx = ApplicationManagerEx.getApplicationEx()
            if (applicationEx.isDispatchThread) {
                isSuccess = ProgressManager.getInstance().runProcessWithProgressSynchronously(
                        task,
                        "Compute class names for declaration $declarationName",
                        true,
                        myDebugProcess.project)
            }
            else {
                ProgressManager.getInstance().runProcess(task, EmptyProgressIndicator())
            }

            if (!isSuccess) {
                XDebugSessionImpl.NOTIFICATION_GROUP.createNotification(
                        "Debugger can skip some executions of $declarationName because the computation of class names was interrupted",
                        MessageType.WARNING
                ).notify(myDebugProcess.project)
            }

            val results = searchResult.map { transformer(it) }
            return ComputedClassNames(results.flatMap { it.classNames }, shouldBeCached = results.all { it.shouldBeCached })
        }
    }

    private fun checkIfInline(declaration: KtDeclaration, bindingContext: BindingContext): Boolean {
        val descriptor = bindingContext.get(BindingContext.DECLARATION_TO_DESCRIPTOR, declaration) ?: return false
        return when (descriptor) {
            is FunctionDescriptor -> InlineUtil.isInline(descriptor)
            is PropertyDescriptor -> InlineUtil.hasInlineAccessors(descriptor)
            else -> false
        }
    }

    private fun getScopeForInlineDeclarationUsages(inlineDeclaration: KtDeclaration): GlobalSearchScope {
        val virtualFile = runReadAction { inlineDeclaration.containingFile.virtualFile }
        return if (virtualFile != null && ProjectRootsUtil.isLibraryFile(myDebugProcess.project, virtualFile)) {
            myDebugProcess.searchScope.uniteWith(
                    KotlinSourceFilterScope.librarySources(GlobalSearchScope.allScope(myDebugProcess.project), myDebugProcess.project))
        }
        else {
            myDebugProcess.searchScope
        }
    }
}