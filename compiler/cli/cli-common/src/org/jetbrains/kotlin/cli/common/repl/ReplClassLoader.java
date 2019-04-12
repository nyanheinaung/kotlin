/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.resolve.jvm.JvmClassName;
import org.jetbrains.org.objectweb.asm.ClassReader;
import org.jetbrains.org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.Map;

public class ReplClassLoader extends ClassLoader {

    private final Map<JvmClassName, byte[]> classes = Maps.newLinkedHashMap();

    public ReplClassLoader(@NotNull ClassLoader parent) {
        super(parent);
    }

    @NotNull
    @Override
    protected Class<?> findClass(@NotNull String name) throws ClassNotFoundException {
        byte[] classBytes = classes.get(JvmClassName.byFqNameWithoutInnerClasses(name));
        if (classBytes != null) {
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        else {
            return super.findClass(name);
        }
    }

    public void addClass(@NotNull JvmClassName className, @NotNull byte[] bytes) {
        byte[] oldBytes = classes.put(className, bytes);
        if (oldBytes != null) {
            throw new IllegalStateException("Rewrite at key " + className);
        }
    }

    public void dumpClasses(@NotNull PrintWriter writer) {
        for (byte[] classBytes : classes.values()) {
            new ClassReader(classBytes).accept(new TraceClassVisitor(writer), 0);
        }
    }

}
