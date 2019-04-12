/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor
import com.intellij.openapi.vfs.VfsUtil

import java.io.File

open class KotlinJdkAndLibraryProjectDescriptor(private val libraryFiles: List<File>) : KotlinLightProjectDescriptor() {

    constructor(libraryFile: File) : this(listOf(libraryFile))

    init {
        for (libraryFile in libraryFiles) {
            assert(libraryFile.exists()) { "Library file doesn't exist: " + libraryFile.absolutePath }
        }
    }

    override fun getModuleType(): ModuleType<*> = StdModuleTypes.JAVA

    override fun getSdk(): Sdk? = PluginTestCaseBase.mockJdk()

    override fun configureModule(module: Module, model: ModifiableRootModel) {
        val editor = NewLibraryEditor()
        editor.name = LIBRARY_NAME
        for (libraryFile in libraryFiles) {
            editor.addRoot(VfsUtil.getUrlForLibraryRoot(libraryFile), OrderRootType.CLASSES)
        }

        ConfigLibraryUtil.addLibrary(editor, model)
    }

    companion object {
        val LIBRARY_NAME = "myLibrary"
    }
}
