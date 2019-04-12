/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.inline.FileMapping;
import org.jetbrains.kotlin.codegen.serialization.JvmSerializationBindings;
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin;
import org.jetbrains.org.objectweb.asm.AnnotationVisitor;
import org.jetbrains.org.objectweb.asm.ClassVisitor;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.MethodVisitor;

public interface ClassBuilder {
    @NotNull
    FieldVisitor newField(
            @NotNull JvmDeclarationOrigin origin,
            int access,
            @NotNull String name,
            @NotNull String desc,
            @Nullable String signature,
            @Nullable Object value
    );

    @NotNull
    MethodVisitor newMethod(
            @NotNull JvmDeclarationOrigin origin,
            int access,
            @NotNull String name,
            @NotNull String desc,
            @Nullable String signature,
            @Nullable String[] exceptions
    );

    @NotNull
    JvmSerializationBindings getSerializationBindings();

    @NotNull
    AnnotationVisitor newAnnotation(@NotNull String desc, boolean visible);

    void done();

    @NotNull
    ClassVisitor getVisitor();

    void defineClass(
            @Nullable PsiElement origin,
            int version,
            int access,
            @NotNull String name,
            @Nullable String signature,
            @NotNull String superName,
            @NotNull String[] interfaces
    );

    void visitSource(@NotNull String name, @Nullable String debug);

    void visitOuterClass(@NotNull String owner, @Nullable String name, @Nullable String desc);

    void visitInnerClass(@NotNull String name, @Nullable String outerName, @Nullable String innerName, int access);

    @NotNull
    String getThisName();

    void addSMAP(FileMapping mapping);
}
