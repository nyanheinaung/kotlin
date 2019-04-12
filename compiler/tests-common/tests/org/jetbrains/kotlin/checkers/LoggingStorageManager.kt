/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers

import org.jetbrains.kotlin.storage.ObservableStorageManager
import org.jetbrains.kotlin.storage.StorageManager
import java.lang.reflect.Field
import java.lang.reflect.GenericDeclaration

class LoggingStorageManager(
        private val delegate: StorageManager,
        private val callHandler: (lambda: Any, call: LoggingStorageManager.CallData) -> Unit) : ObservableStorageManager(delegate) {

    class CallData(
            val fieldOwner: Any?,
            val field: Field?,
            val lambdaCreatedIn: GenericDeclaration?,
            val arguments: List<Any?>,
            val result: Any?
    )

    // Creating objects here because we need a reference to it
    override val <T> (() -> T).observable: () -> T
        get() = object : () -> T {
            override fun invoke(): T {
                val result = this@observable()
                callHandler(this@observable, computeCallerData(this@observable, this, listOf(), result))
                return result
            }
        }

    // Creating objects here because we need a reference to it
    override val <K, V> ((K) -> V).observable: (K) -> V
        get() = object : (K) -> V {
            override fun invoke(p1: K): V {
                val result = this@observable(p1)
                callHandler(this@observable, computeCallerData(this@observable, this, listOf(p1), result))
                return result
            }
        }

    private fun computeCallerData(lambda: Any, wrapper: Any, arguments: List<Any?>, result: Any?): CallData {
        val lambdaClass = lambda::class.java

        val outerClass: Class<out Any?>? = lambdaClass.enclosingClass

        // fields named "this" or "this$0"
        val referenceToOuter = lambdaClass.getAllDeclaredFields().firstOrNull {
            field ->
            field.type == outerClass && field.name!!.contains("this")
        }
        referenceToOuter?.isAccessible = true

        val outerInstance = referenceToOuter?.get(lambda)

        fun Class<*>.findFunctionField(): Field? {
            return this.getAllDeclaredFields().firstOrNull {
                it.type?.name?.startsWith("kotlin.Function") ?: false
            }
        }
        val containingField = if (outerInstance == null) null
                              else outerClass?.getAllDeclaredFields()?.firstOrNull {
                                  field ->
                                  field.isAccessible = true
                                  val value = field.get(outerInstance)
                                  if (value == null) return@firstOrNull false

                                  val valueClass = value::class.java
                                  val functionField = valueClass.findFunctionField()
                                  if (functionField == null) return@firstOrNull false

            functionField.isAccessible = true
                                  val functionValue = functionField.get(value)
                                  functionValue == wrapper
                              }

        if (containingField == null) {
            val wrappedLambdaField = lambdaClass.findFunctionField()
            if (wrappedLambdaField != null) {
                wrappedLambdaField.isAccessible = true
                val wrappedLambda = wrappedLambdaField.get(lambda)
                return CallData(outerInstance, null, enclosingEntity(wrappedLambda::class.java), arguments, result)
            }
        }

        val enclosingEntity = enclosingEntity(lambdaClass)

        return CallData(outerInstance, containingField, enclosingEntity, arguments, result)
    }

    private fun enclosingEntity(klass: Class<out Any>): GenericDeclaration? =
            klass.enclosingConstructor ?: klass.enclosingMethod ?: klass.enclosingClass

    private fun Class<*>.getAllDeclaredFields(): List<Field> {
        val result = arrayListOf<Field>()

        var c = this
        while (true) {
            result.addAll(c.declaredFields.toList())
            @Suppress("UNCHECKED_CAST")
            val superClass = (c as Class<Any>).superclass ?: break
            if (c == superClass) break
            c = superClass
        }

        return result
    }
}
