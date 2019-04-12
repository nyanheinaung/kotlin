/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.KtEnumEntrySuperclassReferenceExpression
import org.jetbrains.kotlin.psi.stubs.KotlinEnumEntrySuperclassReferenceExpressionStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinEnumEntrySuperclassReferenceExpressionStubImpl

class KtEnumEntrySuperClassReferenceExpressionElementType(@NonNls debugName: String) :
    KtStubElementType<KotlinEnumEntrySuperclassReferenceExpressionStub, KtEnumEntrySuperclassReferenceExpression>(
        debugName,
        KtEnumEntrySuperclassReferenceExpression::class.java,
        KotlinEnumEntrySuperclassReferenceExpressionStub::class.java
    ) {

    override fun createStub(
        psi: KtEnumEntrySuperclassReferenceExpression,
        parentStub: StubElement<*>
    ): KotlinEnumEntrySuperclassReferenceExpressionStub {
        return KotlinEnumEntrySuperclassReferenceExpressionStubImpl(parentStub, StringRef.fromString(psi.getReferencedName())!!)
    }

    override fun serialize(stub: KotlinEnumEntrySuperclassReferenceExpressionStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getReferencedName())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): KotlinEnumEntrySuperclassReferenceExpressionStub {
        return KotlinEnumEntrySuperclassReferenceExpressionStubImpl(parentStub, dataStream.readName()!!)
    }
}
