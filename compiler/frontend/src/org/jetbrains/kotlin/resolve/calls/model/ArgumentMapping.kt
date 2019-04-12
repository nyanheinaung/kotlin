/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.types.ErrorUtils

interface ArgumentMapping {
    fun isError(): Boolean
}

object ArgumentUnmapped : ArgumentMapping {
    override fun isError(): Boolean = true
}

enum class ArgumentMatchStatus(val isError: Boolean = true) {
    SUCCESS(false),
    TYPE_MISMATCH(),
    ARGUMENT_HAS_NO_TYPE(),

    // The case when there is no type mismatch, but parameter has uninferred types:
    // fun <T> foo(l: List<T>) {}; val l = foo(emptyList())
    MATCH_MODULO_UNINFERRED_TYPES(),

    UNKNOWN()
}

interface ArgumentMatch : ArgumentMapping {
    val valueParameter: ValueParameterDescriptor
    val status: ArgumentMatchStatus

    override fun isError(): Boolean = status.isError
}

class ArgumentMatchImpl(override val valueParameter: ValueParameterDescriptor) : ArgumentMatch {
    private var _status: ArgumentMatchStatus? = null

    override val status: ArgumentMatchStatus
        get() = _status ?: ArgumentMatchStatus.UNKNOWN

    fun recordMatchStatus(status: ArgumentMatchStatus) {
        _status = status
    }

    fun replaceValueParameter(newValueParameter: ValueParameterDescriptor): ArgumentMatchImpl {
        val newArgumentMatch = ArgumentMatchImpl(newValueParameter)
        newArgumentMatch._status = _status
        return newArgumentMatch
    }
}

//TODO: temporary hack until status.isSuccess is not always correct
fun ResolvedCall<*>.isReallySuccess(): Boolean = status.isSuccess && !ErrorUtils.isError(resultingDescriptor)
