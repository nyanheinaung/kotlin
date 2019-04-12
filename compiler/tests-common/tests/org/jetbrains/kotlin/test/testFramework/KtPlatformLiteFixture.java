/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.testFramework;

import com.intellij.core.CoreEncodingProjectManager;
import com.intellij.mock.MockApplicationEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.ExtensionsArea;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Modifier;

public abstract class KtPlatformLiteFixture extends KtUsefulTestCase {
    protected MockProjectEx myProject;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Extensions.cleanRootArea(getTestRootDisposable());
    }

    public static MockApplicationEx getApplication() {
        return (MockApplicationEx)ApplicationManager.getApplication();
    }

    public void initApplication() {
        MockApplicationEx instance = new MockApplicationEx(getTestRootDisposable());
        ApplicationManager.setApplication(instance, FileTypeManager::getInstance, getTestRootDisposable());
        getApplication().registerService(EncodingManager.class, CoreEncodingProjectManager.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clearFields(this);
        myProject = null;
    }

    protected <T> void registerExtensionPoint(ExtensionPointName<T> extensionPointName, Class<T> aClass) {
        registerExtensionPoint(Extensions.getRootArea(), extensionPointName, aClass);
    }

    private static <T> void registerExtensionPoint(
            ExtensionsArea area, ExtensionPointName<T> extensionPointName,
            Class<? extends T> aClass
    ) {
        String name = extensionPointName.getName();
        if (!area.hasExtensionPoint(name)) {
            ExtensionPoint.Kind kind = aClass.isInterface() || (aClass.getModifiers() & Modifier.ABSTRACT) != 0 ? ExtensionPoint.Kind.INTERFACE : ExtensionPoint.Kind.BEAN_CLASS;
            area.registerExtensionPoint(name, aClass.getName(), kind);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T registerComponentInstance(MutablePicoContainer container, Class<T> key, T implementation) {
        Object old = container.getComponentInstance(key);
        container.unregisterComponent(key);
        container.registerComponentInstance(key, implementation);
        return (T)old;
    }
}
