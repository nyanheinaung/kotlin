/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.refactoring.move.moveDeclarations

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.refactoring.move.MoveCallback
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor
import com.intellij.usageView.UsageInfo
import com.intellij.usageView.UsageViewDescriptor
import com.intellij.util.containers.MultiMap
import com.intellij.util.text.UniqueNameGenerator
import org.jetbrains.kotlin.psi.KtFile

class MoveToKotlinFileProcessor @JvmOverloads constructor (
        project: Project,
        private val sourceFile: KtFile,
        private val targetDirectory: PsiDirectory,
        private val targetFileName: String,
        searchInComments: Boolean,
        searchInNonJavaFiles: Boolean,
        moveCallback: MoveCallback?,
        prepareSuccessfulCallback: Runnable = EmptyRunnable.INSTANCE
) : MoveFilesOrDirectoriesProcessor(project,
                                    arrayOf(sourceFile),
                                    targetDirectory,
                                    true,
                                    searchInComments,
                                    searchInNonJavaFiles,
                                    moveCallback,
                                    prepareSuccessfulCallback) {
    override fun getCommandName() = "Move ${sourceFile.name}"

    override fun createUsageViewDescriptor(usages: Array<out UsageInfo>): UsageViewDescriptor {
        return MoveFilesWithDeclarationsViewDescriptor(arrayOf(sourceFile), targetDirectory)
    }

    override fun preprocessUsages(refUsages: Ref<Array<UsageInfo>>): Boolean {
        val usages = refUsages.get()

        val (conflictUsages, usagesToProcess) = usages.partition { it is ConflictUsageInfo }

        val conflicts = MultiMap<PsiElement, String>()
        for (conflictUsage in conflictUsages) {
            conflicts.putValues(conflictUsage.element, (conflictUsage as ConflictUsageInfo).messages)
        }

        refUsages.set(usagesToProcess.toTypedArray())

        return showConflicts(conflicts, usages)
    }

    // Assign a temporary name to file-under-move to avoid naming conflict during the refactoring
    private fun renameFileTemporarily() {
        if (targetDirectory.findFile(targetFileName) == null) return

        val temporaryName = UniqueNameGenerator.generateUniqueName("temp", "", ".kt") {
            sourceFile.containingDirectory!!.findFile(it) == null
        }
        sourceFile.name = temporaryName
    }

    override fun performRefactoring(usages: Array<UsageInfo>) {
        val needTemporaryRename = targetDirectory.findFile(targetFileName) != null
        if (needTemporaryRename) {
            renameFileTemporarily()
        }

        try {
            super.performRefactoring(usages)
        }
        finally {
            if (needTemporaryRename) {
                sourceFile.name = targetFileName
            }
        }
    }
}
