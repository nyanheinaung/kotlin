/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtDeclarationModifierList extends KtModifierList {
    public KtDeclarationModifierList(@NotNull ASTNode node) {
        super(node);
    }

    public KtDeclarationModifierList(@NotNull KotlinModifierListStub stub) {
        super(stub, KtStubElementTypes.MODIFIER_LIST);
    }
}
