/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.Synthetic
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature

class JvmStaticInCompanionObjectGenerator(
        private val descriptor: FunctionDescriptor,
        private val declarationOrigin: JvmDeclarationOrigin,
        private val state: GenerationState,
        parentBodyCodegen: ImplementationBodyCodegen
) : Function2<ImplementationBodyCodegen, ClassBuilder, Unit> {
    private val typeMapper = state.typeMapper

    init {
        parentBodyCodegen.getContext().accessibleDescriptor(JvmCodegenUtil.getDirectMember(descriptor), null)
    }

    override fun invoke(codegen: ImplementationBodyCodegen, classBuilder: ClassBuilder) {
        val staticFunctionDescriptor = createStaticFunctionDescriptor(descriptor)

        val originElement = declarationOrigin.element
        codegen.functionCodegen.generateMethod(
                Synthetic(originElement, staticFunctionDescriptor),
                staticFunctionDescriptor,
                object : FunctionGenerationStrategy.CodegenBased(state) {
                    override fun skipNotNullAssertionsForParameters(): Boolean = true

                    override fun doGenerateBody(codegen: ExpressionCodegen, signature: JvmMethodSignature) {
                        val iv = codegen.v
                        val classDescriptor = descriptor.containingDeclaration as ClassDescriptor
                        StackValue.singleton(classDescriptor, typeMapper).put(iv)
                        var index = 0
                        val asmMethod = signature.asmMethod
                        for (paramType in asmMethod.argumentTypes) {
                            iv.load(index, paramType)
                            index += paramType.size
                        }
                        if (descriptor is PropertyAccessorDescriptor) {
                            val propertyValue = codegen.intermediateValueForProperty(descriptor.correspondingProperty, false, null, StackValue.none())
                            if (descriptor is PropertyGetterDescriptor) {
                                propertyValue.put(signature.returnType, iv)
                            }
                            else {
                                propertyValue.store(StackValue.onStack(propertyValue.type, propertyValue.kotlinType), iv, true)
                            }
                        }
                        else {
                            val syntheticOrOriginalMethod = typeMapper.mapToCallableMethod(
                                    codegen.context.accessibleDescriptor(descriptor, /* superCallTarget = */ null),
                                    false
                            )
                            syntheticOrOriginalMethod.genInvokeInstruction(iv)
                        }
                        iv.areturn(asmMethod.returnType)
                    }
                }
        )

        if (originElement is KtNamedFunction) {
            codegen.functionCodegen.generateOverloadsWithDefaultValues(originElement, staticFunctionDescriptor, descriptor)
        }
    }

    companion object {
        @JvmStatic
        fun createStaticFunctionDescriptor(descriptor: FunctionDescriptor): FunctionDescriptor {
            val memberDescriptor = if (descriptor is PropertyAccessorDescriptor) descriptor.correspondingProperty else descriptor
            val copies = CodegenUtil.copyFunctions(
                    memberDescriptor,
                    memberDescriptor,
                    descriptor.containingDeclaration.containingDeclaration!!,
                    descriptor.modality,
                    descriptor.visibility,
                    CallableMemberDescriptor.Kind.SYNTHESIZED,
                    false
            )
            return copies[descriptor]!!
        }
    }
}
