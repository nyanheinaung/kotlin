/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.Type
import java.util.*

class Parameters(val parameters: List<ParameterInfo>) : Iterable<ParameterInfo> {

    private val actualDeclShifts: Array<ParameterInfo?>
    private val paramToDeclByteCodeIndex: HashMap<ParameterInfo, Int> = hashMapOf()

    val argsSizeOnStack = parameters.sumBy { it.type.size }

    val realParametersSizeOnStack: Int
        get() = argsSizeOnStack - capturedParametersSizeOnStack

    val capturedParametersSizeOnStack by lazy {
        captured.sumBy { it.type.size }
    }

    val captured by lazy {
        parameters.filterIsInstance<CapturedParamInfo>()
    }

    init {
        val declIndexesToActual = arrayOfNulls<Int>(argsSizeOnStack)
        withIndex().forEach { it ->
            declIndexesToActual[it.value.declarationIndex] = it.index
        }

        actualDeclShifts = arrayOfNulls<ParameterInfo>(argsSizeOnStack)
        var realSize = 0
        for (i in declIndexesToActual.indices) {
            val byDeclarationIndex = get(declIndexesToActual[i] ?: continue)
            actualDeclShifts[realSize] = byDeclarationIndex
            paramToDeclByteCodeIndex.put(byDeclarationIndex, realSize)
            realSize += byDeclarationIndex.type.size
        }
    }

    fun getDeclarationSlot(info: ParameterInfo): Int {
        return paramToDeclByteCodeIndex[info]!!
    }

    fun getParameterByDeclarationSlot(index: Int): ParameterInfo {
        return actualDeclShifts[index]!!
    }

    private fun get(index: Int): ParameterInfo {
        return parameters[index]
    }

    override fun iterator(): Iterator<ParameterInfo> {
        return parameters.iterator()
    }

    val capturedTypes: List<Type>
        get() = captured.map(CapturedParamInfo::type)
}
