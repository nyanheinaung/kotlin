/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast;

import org.jetbrains.kotlin.js.backend.JsToStringGenerationVisitor;
import org.jetbrains.kotlin.js.backend.ast.metadata.HasMetadata;
import org.jetbrains.kotlin.js.util.TextOutputImpl;

abstract class AbstractNode extends HasMetadata implements JsNode {
    @Override
    public String toString() {
        TextOutputImpl out = new TextOutputImpl();
        new JsToStringGenerationVisitor(out).accept(this);
        return out.toString();
    }

    @SuppressWarnings("unchecked")
    protected <T extends HasMetadata & JsNode> T withMetadataFrom(T other) {
        this.copyMetadataFrom(other);
        Object otherSource = other.getSource();
        if (otherSource != null) {
            source(otherSource);
        }
        return (T) this;
    }
}
