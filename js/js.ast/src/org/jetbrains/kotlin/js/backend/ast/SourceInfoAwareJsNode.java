/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast;

public abstract class SourceInfoAwareJsNode extends AbstractNode {
    private Object source;

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public void setSource(Object info) {
        source = info;
    }

    @Override
    public void acceptChildren(JsVisitor visitor) {
    }

    @Override
    public JsNode source(Object info) {
        source = info;
        return this;
    }
}