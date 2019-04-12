/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.framework

import com.intellij.framework.FrameworkTypeEx
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider
import org.jetbrains.kotlin.idea.KotlinIcons
import javax.swing.Icon

class JavaFrameworkType : FrameworkTypeEx("kotlin-java-framework-id") {

    override fun createProvider(): FrameworkSupportInModuleProvider = JavaFrameworkSupportProvider()

    override fun getPresentableName() = "Kotlin/JVM"

    override fun getIcon(): Icon = KotlinIcons.SMALL_LOGO

    companion object {
        val instance: JavaFrameworkType
            get() = FrameworkTypeEx.EP_NAME.findExtension(JavaFrameworkType::class.java)
                ?: error("can't find extension 'JavaFrameworkType'")
    }
}
