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

public abstract class DelegatingClassBuilder implements ClassBuilder {
    @NotNull
    protected abstract ClassBuilder getDelegate();

    @NotNull
    @Override
    public FieldVisitor newField(
            @NotNull JvmDeclarationOrigin origin,
            int access,
            @NotNull String name,
            @NotNull String desc,
            @Nullable String signature,
            @Nullable Object value
    ) {
        return getDelegate().newField(origin, access, name, desc, signature, value);
    }

    @NotNull
    @Override
    public MethodVisitor newMethod(
            @NotNull JvmDeclarationOrigin origin,
            int access,
            @NotNull String name,
            @NotNull String desc,
            @Nullable String signature,
            @Nullable String[] exceptions
    ) {
        return getDelegate().newMethod(origin, access, name, desc, signature, exceptions);
    }

    @NotNull
    @Override
    public JvmSerializationBindings getSerializationBindings() {
        return getDelegate().getSerializationBindings();
    }

    @NotNull
    @Override
    public AnnotationVisitor newAnnotation(@NotNull String desc, boolean visible) {
        return getDelegate().newAnnotation(desc, visible);
    }

    @Override
    public void done() {
        getDelegate().done();
    }

    @NotNull
    @Override
    public ClassVisitor getVisitor() {
        return getDelegate().getVisitor();
    }

    @Override
    public void defineClass(
            @Nullable PsiElement origin,
            int version,
            int access,
            @NotNull String name,
            @Nullable String signature,
            @NotNull String superName,
            @NotNull String[] interfaces
    ) {
        getDelegate().defineClass(origin, version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(@NotNull String name, @Nullable String debug) {
        getDelegate().visitSource(name, debug);
    }

    @Override
    public void visitOuterClass(@NotNull String owner, @Nullable String name, @Nullable String desc) {
        getDelegate().visitOuterClass(owner, name, desc);
    }

    @Override
    public void visitInnerClass(@NotNull String name, @Nullable String outerName, @Nullable String innerName, int access) {
        getDelegate().visitInnerClass(name, outerName, innerName, access);
    }

    @NotNull
    @Override
    public String getThisName() {
        return getDelegate().getThisName();
    }

    @Override
    public void addSMAP(FileMapping mapping) {
        getDelegate().addSMAP(mapping);
    }
}
