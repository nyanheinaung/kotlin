/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.descriptorUtil.declaresOrInheritsDefaultValue
import kotlin.reflect.KParameter
import kotlin.reflect.KType

internal class KParameterImpl(
    val callable: KCallableImpl<*>,
    override val index: Int,
    override val kind: KParameter.Kind,
    computeDescriptor: () -> ParameterDescriptor
) : KParameter {
    private val descriptor: ParameterDescriptor by ReflectProperties.lazySoft(computeDescriptor)

    override val annotations: List<Annotation> by ReflectProperties.lazySoft { descriptor.computeAnnotations() }

    override val name: String?
        get() {
            val valueParameter = descriptor as? ValueParameterDescriptor ?: return null
            if (valueParameter.containingDeclaration.hasSynthesizedParameterNames()) return null
            val name = valueParameter.name
            return if (name.isSpecial) null else name.asString()
        }

    override val type: KType
        get() = KTypeImpl(descriptor.type) {
            val descriptor = descriptor

            if (descriptor is ReceiverParameterDescriptor &&
                callable.descriptor.instanceReceiverParameter == descriptor &&
                callable.descriptor.kind == CallableMemberDescriptor.Kind.FAKE_OVERRIDE
            ) {
                // In case of fake overrides, dispatch receiver type should be computed manually because Caller.parameterTypes returns
                // types from Java reflection where receiver is always the declaring class of the original declaration
                // (not the class where the fake override is generated, which is returned by KParameter.type)
                (callable.descriptor.containingDeclaration as ClassDescriptor).toJavaClass()
                    ?: throw KotlinReflectionInternalError("Cannot determine receiver Java type of inherited declaration: $descriptor")
            } else {
                callable.caller.parameterTypes[index]
            }
        }

    override val isOptional: Boolean
        get() = (descriptor as? ValueParameterDescriptor)?.declaresOrInheritsDefaultValue() ?: false

    override val isVararg: Boolean
        get() = descriptor.let { it is ValueParameterDescriptor && it.varargElementType != null }

    override fun equals(other: Any?) =
        other is KParameterImpl && callable == other.callable && descriptor == other.descriptor

    override fun hashCode() =
        (callable.hashCode() * 31) + descriptor.hashCode()

    override fun toString() =
        ReflectionObjectRenderer.renderParameter(this)
}
