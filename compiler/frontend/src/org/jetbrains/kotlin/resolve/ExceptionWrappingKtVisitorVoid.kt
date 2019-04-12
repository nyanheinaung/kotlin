/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.util.KotlinFrontEndException

class ExceptionWrappingKtVisitorVoid(private val delegate: KtVisitorVoid) : KtVisitorVoid() {
    override fun visitElement(element: PsiElement) {
        element.accept(delegate)
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        try {
            dcl.accept(delegate)
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: KotlinFrontEndException) {
            throw e
        } catch (t: Throwable) {
            val name = try {
                dcl.name
            } catch (e: Throwable) {
                "- error: ${e.message}"
            }
            throw KotlinFrontEndException("Failed to analyze declaration $name", t, dcl)
        }
    }
}
