/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.util

import org.intellij.lang.annotations.Language

@Language("RegExp") const val KT_OR_KTS = """^(.+)\.(kt|kts)$"""
@Language("RegExp") const val KT_OR_KTS_WITHOUT_DOTS_IN_NAME = """^([^.]+)\.(kt|kts)$"""

@Language("RegExp") const val KT_WITHOUT_DOTS_IN_NAME = """^([^.]+)\.kt$"""
