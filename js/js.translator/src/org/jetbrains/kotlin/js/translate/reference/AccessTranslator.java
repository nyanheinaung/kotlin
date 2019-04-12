/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.reference;

import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract entity for language constructs that you can get/set.
 */
public interface AccessTranslator {

    @NotNull
    JsExpression translateAsGet();

    @NotNull
    JsExpression translateAsSet(@NotNull JsExpression setTo);

    //TODO: remove that method
    @NotNull
    AccessTranslator getCached();
}
