/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.AsmUtil.pushDefaultValueOnStack
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodParameterSignature
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

internal class ObjectSuperCallArgumentGenerator(
        private val parameters: List<JvmMethodParameterSignature>,
        private val iv: InstructionAdapter,
        private val superValueParameters: List<ValueParameterDescriptor>,
        private val typeMapper: KotlinTypeMapper,
        offset: Int,
        superConstructorCall: ResolvedCall<ConstructorDescriptor>
) : ArgumentGenerator() {

    private val offsets = IntArray(parameters.size) { -1 }

    init {
        var currentOffset = offset
        superConstructorCall.valueArguments.forEach {
            (descriptor, argument) ->
            if (argument !is DefaultValueArgument) {
                val index = descriptor.index
                offsets[index] = currentOffset
                currentOffset += parameters[index].asmType.size
            }
        }
    }

    override fun generate(
            valueArgumentsByIndex: List<ResolvedValueArgument>,
            actualArgs: List<ResolvedValueArgument>,
            calleeDescriptor: CallableDescriptor?
    ): DefaultCallArgs = super.generate(valueArgumentsByIndex, valueArgumentsByIndex, calleeDescriptor)

    public override fun generateExpression(i: Int, argument: ExpressionValueArgument) {
        generateSuperCallArgument(i)
    }

    public override fun generateDefault(i: Int, argument: DefaultValueArgument) {
        val type = parameters[i].asmType
        pushDefaultValueOnStack(type, iv)
    }

    public override fun generateDefaultJava(i: Int, argument: DefaultValueArgument) {
        val type = parameters[i].asmType
        val value =  superValueParameters[i].findJavaDefaultArgumentValue(type, typeMapper)
        value.put(type, iv)
    }

    public override fun generateVararg(i: Int, argument: VarargValueArgument) {
        generateSuperCallArgument(i)
    }

    private fun generateSuperCallArgument(i: Int) {
        val type = parameters[i].asmType
        if (offsets[i] == -1) {
            throw AssertionError("Unknown parameter value at index $i with type $type")
        }
        iv.load(offsets[i], type)
    }

    override fun reorderArgumentsIfNeeded(args: List<ArgumentAndDeclIndex>) {

    }
}
