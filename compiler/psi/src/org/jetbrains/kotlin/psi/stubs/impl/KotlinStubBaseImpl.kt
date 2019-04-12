/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.NamedStub
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.KtElementImplStub
import org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase
import org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName
import java.lang.reflect.Method
import java.util.ArrayList

val STUB_TO_STRING_PREFIX = "KotlinStub$"

open class KotlinStubBaseImpl<T : KtElementImplStub<*>>(parent: StubElement<*>?, elementType: IStubElementType<*, *>) :
    StubBase<T>(parent, elementType) {

    override fun toString(): String {
        val stubInterface = this::class.java.interfaces.single { it.name.contains("Stub") }
        val propertiesValues = renderPropertyValues(stubInterface)
        if (propertiesValues.isEmpty()) {
            return "$STUB_TO_STRING_PREFIX$stubType"
        }
        val properties = propertiesValues.joinToString(separator = ", ", prefix = "[", postfix = "]")
        return "$STUB_TO_STRING_PREFIX$stubType$properties"
    }

    private fun renderPropertyValues(stubInterface: Class<out Any?>): List<String> {
        return collectProperties(stubInterface).mapNotNull { property -> renderProperty(property) }.sorted()
    }

    private fun collectProperties(stubInterface: Class<*>): Collection<Method> {
        val result = ArrayList<Method>()
        result.addAll(stubInterface.declaredMethods.filter { it.parameterTypes!!.isEmpty() })
        for (baseInterface in stubInterface.interfaces) {
            if (baseInterface in BASE_STUB_INTERFACES) {
                result.addAll(collectProperties(baseInterface))
            }
        }
        return result
    }

    private fun renderProperty(property: Method): String? {
        return try {
            val value = property.invoke(this)
            val name = getPropertyName(property)
            "$name=$value"
        } catch (e: Exception) {
            LOGGER.error(e)
            null
        }
    }

    private fun getPropertyName(method: Method): String {
        val methodName = method.name!!
        if (methodName.startsWith("get")) {
            return methodName.substring(3).decapitalize()
        }
        return methodName
    }

    companion object {
        private val LOGGER: Logger = Logger.getInstance(KotlinStubBaseImpl::class.java)

        private val BASE_STUB_INTERFACES = listOf(
            KotlinStubWithFqName::class.java,
            KotlinClassOrObjectStub::class.java,
            NamedStub::class.java,
            KotlinCallableStubBase::class.java
        )
    }
}
