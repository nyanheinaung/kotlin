/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.FqNameUnsafe;
import org.jetbrains.kotlin.name.Name;

public final class KtNamedDeclarationUtil {
    @Nullable
    public static FqNameUnsafe getUnsafeFQName(@NotNull KtNamedDeclaration namedDeclaration) {
        FqName fqName = namedDeclaration.getFqName();
        return fqName != null ? fqName.toUnsafe() : null;
    }

    @Nullable
    //NOTE: use JetNamedDeclaration#getFqName instead
    /*package private*/ static FqName getFQName(@NotNull KtNamedDeclaration namedDeclaration) {
        Name name = namedDeclaration.getNameAsName();
        if (name == null) {
            return null;
        }

        FqName parentFqName = getParentFqName(namedDeclaration);

        if (parentFqName == null) {
            return null;
        }

        return parentFqName.child(name);
    }

    @Nullable
    public static FqName getParentFqName(@NotNull KtNamedDeclaration namedDeclaration) {
        PsiElement parent = namedDeclaration.getParent();
        if (parent instanceof KtClassBody) {
            // One nesting to JetClassBody doesn't affect to qualified name
            parent = parent.getParent();
        }

        if (parent instanceof KtFile) {
            return ((KtFile) parent).getPackageFqName();
        }
        else if (parent instanceof KtNamedFunction || parent instanceof KtClass) {
            return getFQName((KtNamedDeclaration) parent);
        }
        else if (namedDeclaration instanceof KtParameter) {
            KtClassOrObject constructorClass = KtPsiUtil.getClassIfParameterIsProperty((KtParameter) namedDeclaration);
            if (constructorClass != null) {
                return getFQName(constructorClass);
            }
        }
        else if (parent instanceof KtObjectDeclaration) {
             return getFQName((KtNamedDeclaration) parent);
        }
        return null;
    }

    private KtNamedDeclarationUtil() {
    }
}
