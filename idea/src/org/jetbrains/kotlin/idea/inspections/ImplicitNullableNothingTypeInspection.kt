/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections

import org.jetbrains.kotlin.idea.core.getModalityFromDescriptor
import org.jetbrains.kotlin.idea.intentions.SpecifyTypeExplicitlyIntention
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.typeUtil.isNullableNothing

class ImplicitNullableNothingTypeInspection : IntentionBasedInspection<KtCallableDeclaration>(
        intention = SpecifyTypeExplicitlyIntention::class,
        additionalChecker = { declaration ->
            SpecifyTypeExplicitlyIntention.getTypeForDeclaration(declaration).isNullableNothing() &&
            (declaration.getModalityFromDescriptor() == KtTokens.OPEN_KEYWORD ||
             declaration is KtProperty && declaration.isVar)
        },
        problemText = "Implicit `Nothing?` type"
) {
    override fun inspectionTarget(element: KtCallableDeclaration) = element.nameIdentifier
}
