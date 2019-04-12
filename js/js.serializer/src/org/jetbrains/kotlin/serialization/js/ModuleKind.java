/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.js;

// TODO revisit module dependencies and general design of translator pipeline. This shouldn't be in js.serializer,
// but it's impossible to put this enum anywhere else.
public enum ModuleKind {
    PLAIN,
    AMD,
    COMMON_JS,
    UMD
}
