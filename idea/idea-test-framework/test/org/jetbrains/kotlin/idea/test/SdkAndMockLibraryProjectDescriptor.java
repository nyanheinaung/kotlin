/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.test;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.framework.JSLibraryKind;
import org.jetbrains.kotlin.idea.framework.KotlinSdkType;
import org.jetbrains.kotlin.test.MockLibraryUtil;
import org.jetbrains.kotlin.utils.PathUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class SdkAndMockLibraryProjectDescriptor extends KotlinLightProjectDescriptor {
    public static final String LIBRARY_NAME = "myKotlinLib";

    private final String sourcesPath;
    private final boolean withSources;
    private final boolean withRuntime;
    private final boolean isJsLibrary;
    private final boolean allowKotlinPackage;
    private final List<String> classpath;

    public SdkAndMockLibraryProjectDescriptor(String sourcesPath, boolean withSources) {
        this(sourcesPath, withSources, false, false, false);
    }

    public SdkAndMockLibraryProjectDescriptor(
            String sourcesPath, boolean withSources, boolean withRuntime, boolean isJsLibrary, boolean allowKotlinPackage) {
        this(sourcesPath, withSources, withRuntime, isJsLibrary, allowKotlinPackage, emptyList());
    }

    public SdkAndMockLibraryProjectDescriptor(
            String sourcesPath, boolean withSources, boolean withRuntime, boolean isJsLibrary, boolean allowKotlinPackage, List<String> classpath
    ) {
        this.sourcesPath = sourcesPath;
        this.withSources = withSources;
        this.withRuntime = withRuntime;
        this.isJsLibrary = isJsLibrary;
        this.allowKotlinPackage = allowKotlinPackage;
        this.classpath = classpath;
    }

    @Override
    public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model) {
        List<String> extraOptions = allowKotlinPackage ? Collections.singletonList("-Xallow-kotlin-package") : emptyList();
        File libraryJar =
                isJsLibrary
                ? MockLibraryUtil.compileJsLibraryToJar(sourcesPath, LIBRARY_NAME, withSources, Collections.emptyList())
                : MockLibraryUtil.compileJvmLibraryToJar(sourcesPath, LIBRARY_NAME, withSources, true, extraOptions, classpath);
        String jarUrl = getJarUrl(libraryJar);

        Library.ModifiableModel libraryModel = model.getModuleLibraryTable().getModifiableModel().createLibrary(LIBRARY_NAME).getModifiableModel();
        libraryModel.addRoot(jarUrl, OrderRootType.CLASSES);
        if (withRuntime && !isJsLibrary) {
            libraryModel.addRoot(getJarUrl(PathUtil.getKotlinPathsForDistDirectory().getStdlibPath()), OrderRootType.CLASSES);
        }
        if (isJsLibrary && libraryModel instanceof LibraryEx.ModifiableModelEx) {
            ((LibraryEx.ModifiableModelEx) libraryModel).setKind(JSLibraryKind.INSTANCE);
        }
        if (withSources) {
            libraryModel.addRoot(jarUrl + "src/", OrderRootType.SOURCES);
        }

        libraryModel.commit();

        if (withRuntime && isJsLibrary) {
            KotlinStdJSProjectDescriptor.INSTANCE.configureModule(module, model);
        }
    }

    @Override
    public Sdk getSdk() {
        return isJsLibrary ? KotlinSdkType.INSTANCE.createSdkWithUniqueName(emptyList()) : PluginTestCaseBase.mockJdk();
    }

    @NotNull
    private static String getJarUrl(@NotNull File libraryJar) {
        return "jar://" + FileUtilRt.toSystemIndependentName(libraryJar.getAbsolutePath()) + "!/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SdkAndMockLibraryProjectDescriptor that = (SdkAndMockLibraryProjectDescriptor) o;

        if (withSources != that.withSources) return false;
        if (withRuntime != that.withRuntime) return false;
        if (isJsLibrary != that.isJsLibrary) return false;
        if (!sourcesPath.equals(that.sourcesPath)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourcesPath.hashCode();
        result = 31 * result + (withSources ? 1 : 0);
        result = 31 * result + (withRuntime ? 1 : 0);
        result = 31 * result + (isJsLibrary ? 1 : 0);
        return result;
    }
}
