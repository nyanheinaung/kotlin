/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading;

import java.io.File;

@SuppressWarnings("UnusedParameters")
public abstract class ClassHandler {
    public byte[] instrument(String resourceName, byte[] data) {
        return data;
    }

    public void beforeDefineClass(String name, int sizeInBytes) {}
    public void afterDefineClass(String name) {}

    public void beforeLoadJar(File jarFile) {}
    public void afterLoadJar(File jarFile) {}
}
