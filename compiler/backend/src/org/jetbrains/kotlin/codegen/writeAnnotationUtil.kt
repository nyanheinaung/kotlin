/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.config.JvmAnalysisFlags
import org.jetbrains.kotlin.load.java.JvmAnnotationNames
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmBytecodeBinaryVersion
import org.jetbrains.org.objectweb.asm.AnnotationVisitor

fun writeKotlinMetadata(
    cb: ClassBuilder,
    state: GenerationState,
    kind: KotlinClassHeader.Kind,
    extraFlags: Int,
    action: (AnnotationVisitor) -> Unit
) {
    val av = cb.newAnnotation(JvmAnnotationNames.METADATA_DESC, true)
    av.visit(JvmAnnotationNames.METADATA_VERSION_FIELD_NAME, state.metadataVersion.toArray())
    av.visit(JvmAnnotationNames.BYTECODE_VERSION_FIELD_NAME, JvmBytecodeBinaryVersion.INSTANCE.toArray())
    av.visit(JvmAnnotationNames.KIND_FIELD_NAME, kind.id)
    var flags = extraFlags
    if (state.languageVersionSettings.isPreRelease()) {
        flags = flags or JvmAnnotationNames.METADATA_PRE_RELEASE_FLAG
    }
    if (state.languageVersionSettings.getFlag(JvmAnalysisFlags.strictMetadataVersionSemantics)) {
        flags = flags or JvmAnnotationNames.METADATA_STRICT_VERSION_SEMANTICS_FLAG
    }
    if (flags != 0) {
        av.visit(JvmAnnotationNames.METADATA_EXTRA_INT_FIELD_NAME, flags)
    }
    action(av)
    av.visitEnd()
}

fun writeSyntheticClassMetadata(cb: ClassBuilder, state: GenerationState) {
    writeKotlinMetadata(cb, state, KotlinClassHeader.Kind.SYNTHETIC_CLASS, 0) { _ ->
        // Do nothing
    }
}
