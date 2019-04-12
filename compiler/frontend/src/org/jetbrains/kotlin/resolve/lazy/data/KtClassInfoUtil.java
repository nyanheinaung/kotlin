/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.data;

import kotlin.DeprecationLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtObjectDeclaration;

public class KtClassInfoUtil {

    /**
     * @deprecated use {@link #createClassOrObjectInfo(KtClassOrObject)} instead.
     */
    @Deprecated
    @kotlin.Deprecated(message = "Use createClassOrObjectInfo(KtClassOrObject) instead", level = DeprecationLevel.ERROR)
    @NotNull
    public static KtClassLikeInfo createClassLikeInfo(@NotNull KtClassOrObject classOrObject) {
        return createClassOrObjectInfo(classOrObject);
    }

    @NotNull
    public static KtClassOrObjectInfo<? extends KtClassOrObject> createClassOrObjectInfo(@NotNull KtClassOrObject classOrObject) {
        if (classOrObject instanceof KtClass) {
            return new KtClassInfo((KtClass) classOrObject);
        }
        if (classOrObject instanceof KtObjectDeclaration) {
            return new KtObjectInfo((KtObjectDeclaration) classOrObject);
        }
        throw new IllegalArgumentException("Unknown declaration type: " + classOrObject + classOrObject.getText());
    }
}
