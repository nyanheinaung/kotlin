/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.StackValue

class CapturedParamInfo : ParameterInfo {
    val desc: CapturedParamDesc
    val newFieldName: String
    val isSkipInConstructor: Boolean

    //Now used only for bound function reference receiver
    var isSynthetic: Boolean = false

    val originalFieldName: String
        get() = desc.fieldName

    val containingLambdaName: String
        get() = desc.containingLambdaName

    constructor(desc: CapturedParamDesc, newFieldName: String, skipped: Boolean, index: Int, remapIndex: Int) : super(
        desc.type,
        skipped,
        index,
        remapIndex,
        -1
    ) {
        this.desc = desc
        this.newFieldName = newFieldName
        this.isSkipInConstructor = false
    }

    constructor(
        desc: CapturedParamDesc,
        newFieldName: String,
        skipped: Boolean,
        index: Int,
        remapIndex: StackValue?,
        skipInConstructor: Boolean,
        declarationIndex: Int
    ) : super(desc.type, skipped, index, remapIndex, declarationIndex) {
        this.desc = desc
        this.newFieldName = newFieldName
        this.isSkipInConstructor = skipInConstructor
    }

    fun cloneWithNewDeclarationIndex(newDeclarationIndex: Int): CapturedParamInfo {
        val result = CapturedParamInfo(
            desc, newFieldName, isSkipped, index, remapValue, isSkipInConstructor, newDeclarationIndex
        )
        result.functionalArgument = functionalArgument
        result.isSynthetic = isSynthetic
        return result
    }

    companion object {

        fun isSynthetic(info: ParameterInfo): Boolean {
            return info is CapturedParamInfo && info.isSynthetic
        }
    }
}
