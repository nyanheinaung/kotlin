/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.builtins.KotlinBuiltIns.COLLECTIONS_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.fileClasses.internalNameWithoutInnerClasses
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.JvmPrimitiveType
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class IteratorNext : IntrinsicMethod() {
    override fun toCallable(method: CallableMethod): Callable {
        val type = AsmUtil.unboxType(method.returnType)
        return object : IntrinsicCallable(type, listOf(), AsmTypes.OBJECT_TYPE, null) {
            override fun invokeIntrinsic(v: InstructionAdapter) {
                val primitiveClassName = getKotlinPrimitiveClassName(returnType)
                v.invokevirtual(
                        getPrimitiveIteratorType(primitiveClassName).internalName,
                        "next${primitiveClassName.asString()}",
                        "()" + returnType.descriptor,
                        false
                )
            }
        }
    }

    companion object {
        // Type.CHAR_TYPE -> "Char"
        private fun getKotlinPrimitiveClassName(type: Type): Name {
            return JvmPrimitiveType.get(type.className).primitiveType.typeName
        }

        // "Char" -> type for kotlin.collections.CharIterator
        fun getPrimitiveIteratorType(primitiveClassName: Name): Type {
            val iteratorName = Name.identifier(primitiveClassName.asString() + "Iterator")
            return Type.getObjectType(COLLECTIONS_PACKAGE_FQ_NAME.child(iteratorName).internalNameWithoutInnerClasses)
        }
    }
}
