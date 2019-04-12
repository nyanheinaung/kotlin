/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.quickfix.createFromUsage.createTypeAlias

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiPackage
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.core.CollectingNameValidator
import org.jetbrains.kotlin.idea.core.KotlinNameSuggester
import org.jetbrains.kotlin.idea.core.NewDeclarationNameValidator
import org.jetbrains.kotlin.idea.project.languageVersionSettings
import org.jetbrains.kotlin.idea.quickfix.IntentionActionPriority
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactoryWithDelegate
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.createClass.CreateClassFromTypeReferenceActionFactory
import org.jetbrains.kotlin.idea.quickfix.createFromUsage.createClass.getTypeConstraintInfo
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypeAndBranch
import org.jetbrains.kotlin.types.typeUtil.containsError

object CreateTypeAliasFromTypeReferenceActionFactory : KotlinSingleIntentionActionFactoryWithDelegate<KtUserType, TypeAliasInfo>(IntentionActionPriority.LOW) {
    override fun getElementOfInterest(diagnostic: Diagnostic) = CreateClassFromTypeReferenceActionFactory.getElementOfInterest(diagnostic)

    override fun extractFixData(element: KtUserType, diagnostic: Diagnostic): TypeAliasInfo? {
        if (element.getParentOfTypeAndBranch<KtUserType>(true) { qualifier } != null) return null
        if (!element.languageVersionSettings.supportsFeature(LanguageFeature.TypeAliases)) return null

        val classInfo = CreateClassFromTypeReferenceActionFactory.extractFixData(element, diagnostic) ?: return null
        val targetParent = classInfo.applicableParents.singleOrNull { it !is KtDeclaration && it !is PsiPackage } ?: return null
        if ((targetParent as? PsiClass)?.language == JavaLanguage.INSTANCE) return null

        val expectedType = getTypeConstraintInfo(element)?.upperBound
        if (expectedType != null && expectedType.containsError()) return null

        val validator = CollectingNameValidator(
                filter = NewDeclarationNameValidator(targetParent, null, NewDeclarationNameValidator.Target.FUNCTIONS_AND_CLASSES)
        )
        val typeParameterNames = KotlinNameSuggester.suggestNamesForTypeParameters(classInfo.typeArguments.size, validator)
        return TypeAliasInfo(classInfo.name, targetParent, typeParameterNames, expectedType)
    }

    override fun createFix(originalElement: KtUserType, data: TypeAliasInfo) = CreateTypeAliasFromUsageFix(originalElement, data)
}