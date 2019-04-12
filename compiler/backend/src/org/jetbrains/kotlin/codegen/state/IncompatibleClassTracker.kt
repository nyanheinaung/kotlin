/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.state

import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinaryClass
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmBytecodeBinaryVersion
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.serialization.deserialization.IncompatibleVersionErrorData
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

interface IncompatibleClassTracker {
    fun record(binaryClass: KotlinJvmBinaryClass)

    object DoNothing : IncompatibleClassTracker {
        override fun record(binaryClass: KotlinJvmBinaryClass) {
        }
    }
}

class IncompatibleClassTrackerImpl(val trace: BindingTrace) : IncompatibleClassTracker {
    private val classes = linkedSetOf<String>()

    override fun record(binaryClass: KotlinJvmBinaryClass) {
        if (classes.add(binaryClass.location)) {
            val errorData = IncompatibleVersionErrorData(
                    binaryClass.classHeader.bytecodeVersion,
                    JvmBytecodeBinaryVersion.INSTANCE,
                    binaryClass.location,
                    binaryClass.classId
            )
            trace.record(BYTECODE_VERSION_ERRORS, binaryClass.location, errorData)
        }
    }

    companion object {
        @JvmField
        val BYTECODE_VERSION_ERRORS: WritableSlice<String, IncompatibleVersionErrorData<JvmBytecodeBinaryVersion>> = Slices.createCollectiveSlice()
    }
}
