/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VfsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.forTestCompile.ForTestCompileRuntime;

import java.io.File;

public class ProjectDescriptorWithStdlibSources extends KotlinWithJdkAndRuntimeLightProjectDescriptor {
    @NotNull
    public static final ProjectDescriptorWithStdlibSources INSTANCE = new ProjectDescriptorWithStdlibSources();

    @Override
    public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model) {
        super.configureModule(module, model);

        Library library = model.getModuleLibraryTable().getLibraryByName(Companion.getLIBRARY_NAME());
        assert library != null;
        Library.ModifiableModel modifiableModel = library.getModifiableModel();
        modifiableModel.addRoot(VfsUtil.getUrlForLibraryRoot(ForTestCompileRuntime.runtimeSourcesJarForTests()), OrderRootType.SOURCES);
        modifiableModel.commit();
    }
}
