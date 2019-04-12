/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.metadata

import org.jetbrains.kotlin.metadata.builtins.BuiltInsBinaryVersion
import org.jetbrains.kotlin.serialization.KotlinSerializerExtensionBase
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInSerializerProtocol

class MetadataSerializerExtension(
    override val metadataVersion: BuiltInsBinaryVersion
) : KotlinSerializerExtensionBase(BuiltInSerializerProtocol) {
    override fun shouldUseTypeTable(): Boolean = true
}
