/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.js

import org.jetbrains.kotlin.utils.JsMetadataVersion

class PackagesWithHeaderMetadata(
    val header: ByteArray,
    val packages: List<ByteArray>,
    val metadataVersion: JsMetadataVersion
)
