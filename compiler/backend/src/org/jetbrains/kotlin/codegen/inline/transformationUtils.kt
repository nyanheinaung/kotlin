/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import com.intellij.openapi.util.Pair
import org.jetbrains.kotlin.codegen.FieldInfo
import org.jetbrains.org.objectweb.asm.Type

class NewJavaField(val name: String, val type: Type, val skip: Boolean)

fun getNewFieldsToGenerate(params: List<CapturedParamInfo>): List<NewJavaField> {
    return params.filter {
        //not inlined
        it.functionalArgument !is LambdaInfo
    }.map {
        NewJavaField(it.newFieldName, it.type, it.isSkipInConstructor)
    }
}

fun transformToFieldInfo(lambdaType: Type, newFields: List<NewJavaField>): List<FieldInfo> {
    return newFields.map { field ->
        FieldInfo.createForHiddenField(lambdaType, field.type, field.name)
    }
}

fun filterSkipped(fields: List<NewJavaField>): List<NewJavaField> {
    return fields.filter { !it.skip }
}

fun toNameTypePair(fields: List<NewJavaField>): List<Pair<String, Type>> = fields.map { Pair(it.name, it.type) }
