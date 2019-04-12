/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.parser;

import org.jetbrains.kotlin.kdoc.psi.impl.KDocName;
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection;
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag;

public class KDocElementTypes {
    public static final KDocElementType KDOC_SECTION = new KDocElementType("KDOC_SECTION", KDocSection.class);
    public static final KDocElementType KDOC_TAG = new KDocElementType("KDOC_TAG", KDocTag.class);
    public static final KDocElementType KDOC_NAME = new KDocElementType("KDOC_NAME", KDocName.class);
}
