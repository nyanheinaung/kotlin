/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.backend.common.output.OutputFile;
import org.jetbrains.kotlin.utils.ExceptionUtilsKt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

public class GeneratedClassLoader extends URLClassLoader {

    private ClassFileFactory factory;

    public GeneratedClassLoader(@NotNull ClassFileFactory factory, ClassLoader parentClassLoader, URL... urls) {
        super(urls, parentClassLoader);
        this.factory = factory;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        OutputFile outputFile = factory.get(name);
        if (outputFile != null) {
            return new ByteArrayInputStream(outputFile.asByteArray());
        }
        return super.getResourceAsStream(name);
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> fromParent = super.findResources(name);

        URL url = createFakeURLForResource(name);
        if (url == null) return fromParent;

        List<URL> fromMe = Collections.singletonList(url);
        List<URL> result = fromParent.hasMoreElements()
                           ? CollectionsKt.plus(fromMe, Collections.list(fromParent))
                           : fromMe;
        return Collections.enumeration(result);
    }

    @Override
    public URL findResource(String name) {
        URL url = createFakeURLForResource(name);
        return url != null ? url : super.findResource(name);
    }

    @Nullable
    private URL createFakeURLForResource(@NotNull String name) {
        try {
            OutputFile outputFile = factory.get(name);
            return outputFile == null
                   ? null
                   : BytesUrlUtils.createBytesUrl(outputFile.asByteArray());
        } catch (IOException e) {
            throw ExceptionUtilsKt.rethrow(e);
        }
    }

    @NotNull
    @Override
    protected Class<?> findClass(@NotNull String name) throws ClassNotFoundException {
        String classFilePath = name.replace('.', '/') + ".class";

        OutputFile outputFile = factory.get(classFilePath);
        if (outputFile != null) {
            byte[] bytes = outputFile.asByteArray();
            int lastDot = name.lastIndexOf('.');
            if (lastDot >= 0) {
                String pkgName = name.substring(0, lastDot);
                if (getPackage(pkgName) == null) {
                    definePackage(pkgName, new Manifest(), null);
                }
            }
            return defineClass(name, bytes, 0, bytes.length);
        }

        return super.findClass(name);
    }

    public void dispose() {
        factory = null;
    }

    @NotNull
    public List<OutputFile> getAllGeneratedFiles() {
        return factory.asList();
    }
}
