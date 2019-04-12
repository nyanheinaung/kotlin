/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android;

import com.android.ide.common.blame.parser.PatternAwareOutputParser;
import com.android.ide.common.blame.parser.util.OutputLineReader;
import com.android.ide.common.blame.Message;
import com.android.utils.ILogger;

import java.util.List;

public class KotlinOutputParser implements PatternAwareOutputParser {
    @Override
    public boolean parse(String s, OutputLineReader reader, List<Message> list, ILogger logger) {
        return KotlinOutputParserHelperKt.parse(s, reader, list);
    }
}
