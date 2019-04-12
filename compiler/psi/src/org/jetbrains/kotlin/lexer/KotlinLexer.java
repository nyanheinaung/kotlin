/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class KotlinLexer extends FlexAdapter {
    public KotlinLexer() {
        super(new _JetLexer((Reader) null));
    }
}
