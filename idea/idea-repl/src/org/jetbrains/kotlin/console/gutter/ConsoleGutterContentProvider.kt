/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.console.gutter

import com.intellij.execution.console.BasicGutterContentProvider
import com.intellij.openapi.editor.Editor

class ConsoleGutterContentProvider : BasicGutterContentProvider() {
    /**
     *  This method overriding is needed to prevent [BasicGutterContentProvider] from adding some strange unicode
     *  symbols of zero width and to ease range highlighting.
     */
    override fun beforeEvaluate(editor: Editor) = Unit
}