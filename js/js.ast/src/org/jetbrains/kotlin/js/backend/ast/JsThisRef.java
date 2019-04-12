/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast;

import org.jetbrains.annotations.NotNull;

public final class JsThisRef extends JsLiteral.JsValueLiteral {
    @Override
    public void accept(JsVisitor v) {
        v.visitThis(this);
    }

    @Override
    public void traverse(JsVisitorWithContext v, JsContext ctx) {
        v.visit(this, ctx);
        v.endVisit(this, ctx);
    }

    @NotNull
    @Override
    public JsThisRef deepCopy() {
        return new JsThisRef().withMetadataFrom(this);
    }
}
