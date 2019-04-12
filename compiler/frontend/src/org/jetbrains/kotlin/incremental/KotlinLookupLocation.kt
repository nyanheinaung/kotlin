/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.diagnostics.DiagnosticUtils.getLineAndColumnInPsiFile
import org.jetbrains.kotlin.incremental.components.LocationInfo
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.Position
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.doNotAnalyze

class KotlinLookupLocation(val element: KtElement) : LookupLocation {

    override val location: LocationInfo?
        get() {
            val containingJetFile = element.containingKtFile

            if (containingJetFile.doNotAnalyze != null) return null

            return object : LocationInfo {
                override val filePath = containingJetFile.virtualFilePath

                override val position: Position
                    get() = getLineAndColumnInPsiFile(containingJetFile, element.textRange).let { Position(it.line, it.column) }
            }
        }
}
