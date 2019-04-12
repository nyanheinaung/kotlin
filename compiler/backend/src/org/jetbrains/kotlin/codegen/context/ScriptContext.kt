/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context

import org.jetbrains.kotlin.codegen.FieldInfo
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ScriptDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtScript
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.org.objectweb.asm.Type

class ScriptContext(
    typeMapper: KotlinTypeMapper,
    val scriptDescriptor: ScriptDescriptor,
    val earlierScripts: List<ScriptDescriptor>,
    contextDescriptor: ClassDescriptor,
    parentContext: CodegenContext<*>?
) : ScriptLikeContext(typeMapper, contextDescriptor, parentContext) {
    val lastStatement: KtExpression?

    val resultFieldInfo: FieldInfo
        get() {
            assert(state.replSpecific.shouldGenerateScriptResultValue) { "Should not be called unless 'resultFieldName' is set" }
            val scriptResultFieldName = state.replSpecific.scriptResultFieldName!!
            val fieldType = state.replSpecific.resultType?.let { state.typeMapper.mapType(it) } ?: AsmTypes.OBJECT_TYPE
            return FieldInfo.createForHiddenField(
                state.typeMapper.mapClass(scriptDescriptor),
                fieldType,
                state.replSpecific.resultType,
                scriptResultFieldName
            )
        }

    val script = DescriptorToSourceUtils.getSourceFromDescriptor(scriptDescriptor) as KtScript?
        ?: error("Declaration should be present for script: $scriptDescriptor")

    init {
        val lastDeclaration = script.declarations.lastOrNull()
        if (lastDeclaration is KtAnonymousInitializer) {
            this.lastStatement = lastDeclaration.body
        } else {
            this.lastStatement = null
        }
    }

    val ctorValueParametersStart = if (earlierScripts.isNotEmpty()) 1 else 0

    private val ctorImplicitReceiversParametersStart =
        ctorValueParametersStart + (scriptDescriptor.getSuperClassNotAny()?.unsubstitutedPrimaryConstructor?.valueParameters?.size ?: 0)

    private val ctorProvidedPropertiesParametersStart =
        ctorImplicitReceiversParametersStart + scriptDescriptor.implicitReceivers.size

    fun getImplicitReceiverName(index: Int): String =
        scriptDescriptor.unsubstitutedPrimaryConstructor.valueParameters[ctorImplicitReceiversParametersStart + index].name.identifier

    fun getImplicitReceiverType(index: Int): Type? {
        val receiverParam = scriptDescriptor.unsubstitutedPrimaryConstructor.valueParameters[ctorImplicitReceiversParametersStart + index]
        return state.typeMapper.mapType(receiverParam.type)
    }

    fun getProvidedPropertyName(index: Int): String =
        scriptDescriptor.unsubstitutedPrimaryConstructor.valueParameters[ctorProvidedPropertiesParametersStart + index].name.identifier

    fun getProvidedPropertyType(index: Int): Type = typeMapper.mapType(scriptDescriptor.scriptProvidedProperties[index].type)

    override fun getOuterReceiverExpression(prefix: StackValue?, thisOrOuterClass: ClassDescriptor): StackValue {
        if (thisOrOuterClass.containingDeclaration == scriptDescriptor) {
            return prefix ?: StackValue.LOCAL_0
        }
        receiverDescriptors.forEachIndexed { index, outerReceiver ->
            if (outerReceiver == thisOrOuterClass) {
                return getImplicitReceiverType(index)?.let { type ->
                    val owner = typeMapper.mapType(scriptDescriptor)
                    StackValue.field(type, owner, getImplicitReceiverName(index), false, prefix ?: StackValue.LOCAL_0)
                } ?: error("Invalid script receiver: ${thisOrOuterClass.fqNameSafe}")
            }
        }
        error("Script receiver not found: ${thisOrOuterClass.fqNameSafe}")
    }

    val receiverDescriptors: List<ClassDescriptor>
        get() = scriptDescriptor.implicitReceivers

    fun getScriptFieldName(scriptDescriptor: ScriptDescriptor): String {
        val index = earlierScripts.indexOf(scriptDescriptor)
        return if (index >= 0) "script$" + (index + 1)
        else "\$\$importedScript${scriptDescriptor.name.identifier}"
    }

    override fun toString(): String {
        return "Script: " + contextDescriptor.name.asString()
    }
}

private val Class<*>.classId: ClassId
    get() = enclosingClass?.classId?.createNestedClassId(Name.identifier(simpleName)) ?: ClassId.topLevel(FqName(name))
