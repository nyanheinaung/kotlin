/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassKind;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtEnumEntry;
import org.jetbrains.kotlin.psi.KtTypeParameterList;

public class KtClassInfo extends KtClassOrObjectInfo<KtClass> {
    private final ClassKind kind;

    protected KtClassInfo(@NotNull KtClass classOrObject) {
        super(classOrObject);
        if (element instanceof KtEnumEntry) {
            this.kind = ClassKind.ENUM_ENTRY;
        }
        else if (element.isInterface()) {
            this.kind = ClassKind.INTERFACE;
        }
        else if (element.isEnum()) {
            this.kind = ClassKind.ENUM_CLASS;
        }
        else if (element.isAnnotation()) {
            this.kind = ClassKind.ANNOTATION_CLASS;
        }
        else {
            this.kind = ClassKind.CLASS;
        }
    }

    @Nullable
    @Override
    public KtTypeParameterList getTypeParameterList() {
        return element.getTypeParameterList();
    }

    @NotNull
    @Override
    public ClassKind getClassKind() {
        return kind;
    }
}
