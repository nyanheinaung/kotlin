/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.general;

import org.jetbrains.kotlin.js.backend.ast.JsProgram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.js.translate.context.Namer;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.resolve.BindingContext;

public abstract class AbstractTranslator {

    @NotNull
    private final TranslationContext context;

    protected AbstractTranslator(@NotNull TranslationContext context) {
        this.context = context;
    }

    @NotNull
    protected JsProgram program() {
        return context.program();
    }

    @NotNull
    protected TranslationContext context() {
        return context;
    }

    @NotNull
    protected BindingContext bindingContext() {
        return context.bindingContext();
    }

    @NotNull
    protected Namer namer() {
        return context.namer();
    }
}
