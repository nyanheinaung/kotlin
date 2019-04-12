/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.script.dependencies

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.compiled.*
import com.intellij.psi.util.MethodSignatureUtil
import org.jetbrains.kotlin.idea.core.script.ScriptDependenciesManager

class ScriptDependencySourceNavigationPolicyForJavaClasses : ClsCustomNavigationPolicyEx() {
    override fun getNavigationElement(clsClass: ClsClassImpl): PsiClass? {
        val containingClass = clsClass.containingClass as? ClsClassImpl
        if (containingClass != null) {
            return getNavigationElement(containingClass)?.findInnerClassByName(clsClass.name, false)
        }

        val clsFileImpl = clsClass.containingFile as? ClsFileImpl ?: return null
        return getFileNavigationElement(clsFileImpl)?.classes?.singleOrNull()
    }

    override fun getNavigationElement(clsMethod: ClsMethodImpl): PsiElement? {
        val clsClass = getNavigationElement(clsMethod.containingClass as ClsClassImpl) ?: return null
        return clsClass.findMethodsByName(clsMethod.name, false)
                .firstOrNull { MethodSignatureUtil.areParametersErasureEqual(it, clsMethod) }
    }

    override fun getNavigationElement(clsField: ClsFieldImpl): PsiElement? {
        val srcClass = getNavigationElement(clsField.containingClass as ClsClassImpl) ?: return null
        return srcClass.findFieldByName(clsField.name, false)
    }

    override fun getFileNavigationElement(file: ClsFileImpl): PsiClassOwner? {
        val virtualFile = file.virtualFile
        val project = file.project

        val kotlinScriptConfigurationManager = ScriptDependenciesManager.getInstance(project)
        if (virtualFile !in kotlinScriptConfigurationManager.getAllScriptsClasspathScope()) return null

        val sourceFileName = (file.classes.first() as ClsClassImpl).sourceFileName
        val packageName = file.packageName
        val relativePath = if (packageName.isEmpty()) sourceFileName else packageName.replace('.', '/') + '/' + sourceFileName

        for (root in kotlinScriptConfigurationManager.getAllLibrarySources().filter { it.isValid }) {
            val sourceFile = root.findFileByRelativePath(relativePath)
            if (sourceFile != null && sourceFile.isValid) {
                val sourcePsi = file.manager.findFile(sourceFile)
                if (sourcePsi is PsiClassOwner) {
                    return sourcePsi
                }
            }
        }
        return null
    }
}