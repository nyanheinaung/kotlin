/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.intrinsic.objects

import org.jetbrains.kotlin.builtins.CompanionObjectMapping
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.backend.ast.JsExpression
import org.jetbrains.kotlin.js.translate.context.TranslationContext
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameUnsafe

class DefaultClassObjectIntrinsic( val fqName: FqName): ObjectIntrinsic {
    override fun apply(context: TranslationContext) = context.getReferenceToIntrinsic(fqName.asString())
}

class ObjectIntrinsics {
    private val cache = mutableMapOf<ClassDescriptor, ObjectIntrinsic?>()

    fun getIntrinsic(classDescriptor: ClassDescriptor): ObjectIntrinsic? {
        if (classDescriptor in cache) return cache[classDescriptor]

        return createIntrinsic(classDescriptor).also {
            cache[classDescriptor] = it
        }
    }

    private fun createIntrinsic(classDescriptor: ClassDescriptor): ObjectIntrinsic? {
        if (classDescriptor.fqNameUnsafe == KotlinBuiltIns.FQ_NAMES._enum ||
            !CompanionObjectMapping.isMappedIntrinsicCompanionObject(classDescriptor)
        ) {
            return null
        }

        val containingDeclaration = classDescriptor.containingDeclaration
        val name = Name.identifier(containingDeclaration.name.asString() + "CompanionObject")

        return DefaultClassObjectIntrinsic(FqName("kotlin.js.internal").child(name))
    }
}

interface ObjectIntrinsic {
    fun apply(context: TranslationContext): JsExpression
}
