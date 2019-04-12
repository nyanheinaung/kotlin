/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.lang.UnsupportedOperationException

/**
 * Describes any "this" receiver inside a class
 */
interface ThisClassReceiver : ReceiverValue {
    val classDescriptor: ClassDescriptor
}

/**
 * Same but implicit only
 */
open class ImplicitClassReceiver(
    final override val classDescriptor: ClassDescriptor,
    original: ImplicitClassReceiver? = null
) : ThisClassReceiver, ImplicitReceiver {

    private val original = original ?: this

    override fun getType() = classDescriptor.defaultType

    override val declarationDescriptor = classDescriptor

    override fun equals(other: Any?) = classDescriptor == (other as? ImplicitClassReceiver)?.classDescriptor

    override fun hashCode() = classDescriptor.hashCode()

    override fun toString() = "Class{$type}"

    override fun replaceType(newType: KotlinType) =
            throw UnsupportedOperationException("Replace type should not be called for this receiver")

    override fun getOriginal() = original
}
