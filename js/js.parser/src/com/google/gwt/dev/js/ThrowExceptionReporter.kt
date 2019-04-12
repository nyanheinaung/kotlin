/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.google.gwt.dev.js

import com.google.gwt.dev.js.rhino.*
import com.google.gwt.dev.js.parserExceptions.*

object ThrowExceptionOnErrorReporter : ErrorReporter {
    override fun warning(message: String, startPosition: CodePosition, endPosition: CodePosition) {}

    override fun error(message: String, startPosition: CodePosition, endPosition: CodePosition) =
        throw JsParserException(message, startPosition)
}