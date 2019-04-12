/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl

import com.intellij.psi.*
import org.jetbrains.kotlin.load.java.structure.JavaClassifierType
import org.jetbrains.kotlin.load.java.structure.JavaType

import java.util.ArrayList

class JavaClassifierTypeImpl(psiClassType: PsiClassType) : JavaTypeImpl<PsiClassType>(psiClassType), JavaClassifierType {

    private var resolutionResult: ResolutionResult? = null

    override val classifier: JavaClassifierImpl<*>?
        get() = resolve().classifier

    val substitutor: PsiSubstitutor
        get() = resolve().substitutor

    override val classifierQualifiedName: String
        get() = psi.canonicalText.convertCanonicalNameToQName()

    override val presentableText: String
        get() = psi.presentableText

    override val isRaw: Boolean
        get() = resolve().isRaw

    override// parameters including ones from outer class
    val typeArguments: List<JavaType?>
        get() {
            val classifier = classifier as? JavaClassImpl ?: return emptyList()
            val parameters = getTypeParameters(classifier.psi)

            val substitutor = substitutor

            val result = ArrayList<JavaType?>(parameters.size)
            for (typeParameter in parameters) {
                val substitutedType = substitutor.substitute(typeParameter)
                result.add(substitutedType?.let { JavaTypeImpl.create(it) })
            }

            return result
        }

    private class ResolutionResult(
        val classifier: JavaClassifierImpl<*>?,
        val substitutor: PsiSubstitutor,
        val isRaw: Boolean
    )

    private fun resolve(): ResolutionResult {
        return resolutionResult ?: run {
            val result = psi.resolveGenerics()
            val psiClass = result.element
            val substitutor = result.substitutor
            ResolutionResult(
                psiClass?.let { JavaClassifierImpl.create(it) }, substitutor, PsiClassType.isRaw(result)
            ).apply {
                resolutionResult = this
            }
        }
    }

    // Copy-pasted from PsiUtil.typeParametersIterable
    // The only change is using `Collections.addAll(result, typeParameters)` instead of reversing type parameters of `currentOwner`
    // Result differs in cases like:
    // class Outer<H1> {
    //   class Inner<H2, H3> {}
    // }
    //
    // PsiUtil.typeParametersIterable returns H3, H2, H1
    // But we would like to have H2, H3, H1 as such order is consistent with our type representation
    private fun getTypeParameters(owner: PsiClass): List<PsiTypeParameter> {
        var result: List<PsiTypeParameter>? = null

        var currentOwner: PsiTypeParameterListOwner? = owner
        while (currentOwner != null) {
            val typeParameters = currentOwner.typeParameters
            if (typeParameters.isNotEmpty()) {
                result = result?.let { it + typeParameters } ?: typeParameters.toList()
            }

            if (currentOwner.hasModifierProperty(PsiModifier.STATIC)) break
            currentOwner = currentOwner.containingClass
        }

        return result ?: emptyList()
    }
}
