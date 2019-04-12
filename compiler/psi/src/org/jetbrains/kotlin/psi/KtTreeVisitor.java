/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import org.jetbrains.annotations.NotNull;

public class KtTreeVisitor<D> extends KtVisitor<Void, D> {
    @Override
    public Void visitKtElement(@NotNull KtElement element, D data) {
        element.acceptChildren(this, data);
        return null;
    }

    @Override
    public Void visitKtFile(@NotNull KtFile file, D data) {
        super.visitKtFile(file, data);
        file.acceptChildren(this, data);
        return null;
    }
}
