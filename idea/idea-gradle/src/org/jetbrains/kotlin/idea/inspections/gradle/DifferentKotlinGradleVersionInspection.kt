/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.inspections.gradle

import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.idea.inspections.PluginVersionDependentInspection
import org.jetbrains.kotlin.idea.versions.bundledRuntimeVersion
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.plugins.gradle.codeInspection.GradleBaseInspection
import org.jetbrains.plugins.groovy.codeInspection.BaseInspection
import org.jetbrains.plugins.groovy.codeInspection.BaseInspectionVisitor
import org.jetbrains.plugins.groovy.lang.psi.GroovyFileBase
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrCallExpression

class DifferentKotlinGradleVersionInspection : GradleBaseInspection(), PluginVersionDependentInspection {
    override var testVersionMessage: String? = null
        @TestOnly set

    override fun buildVisitor(): BaseInspectionVisitor = MyVisitor()

    override fun getGroupDisplayName() = BaseInspection.PROBABLE_BUGS

    override fun buildErrorString(vararg args: Any): String {
        return "Kotlin version that is used for building with Gradle (${args[0]}) differs from the one bundled into the IDE plugin (${args[1]})"
    }

    private abstract class VersionFinder : KotlinGradleInspectionVisitor() {
        protected abstract fun onFound(kotlinPluginVersion: String, kotlinPluginStatement: GrCallExpression)

        override fun visitClosure(closure: GrClosableBlock) {
            super.visitClosure(closure)

            val dependenciesCall = closure.getStrictParentOfType<GrMethodCall>() ?: return
            if (dependenciesCall.invokedExpression.text != "dependencies") return

            val buildScriptCall = dependenciesCall.getStrictParentOfType<GrMethodCall>() ?: return
            if (buildScriptCall.invokedExpression.text != "buildscript") return

            val kotlinPluginStatement = GradleHeuristicHelper.findStatementWithPrefix(closure, "classpath").firstOrNull {
                it.text.contains(KOTLIN_PLUGIN_CLASSPATH_MARKER)
            } ?: return

            val kotlinPluginVersion =
                GradleHeuristicHelper.getHeuristicVersionInBuildScriptDependency(kotlinPluginStatement) ?: getResolvedKotlinGradleVersion(
                    closure.containingFile
                ) ?: return

            onFound(kotlinPluginVersion, kotlinPluginStatement)
        }
    }

    private inner class MyVisitor : VersionFinder() {
        private val idePluginVersion by lazy { bundledRuntimeVersion() }

        override fun onFound(kotlinPluginVersion: String, kotlinPluginStatement: GrCallExpression) {
            if (kotlinPluginVersion != idePluginVersion) {
                registerError(kotlinPluginStatement, kotlinPluginVersion, testVersionMessage ?: idePluginVersion)
            }
        }
    }

    companion object {
        fun getKotlinPluginVersion(gradleFile: GroovyFileBase): String? {
            var version: String? = null
            val visitor = object : VersionFinder() {
                override fun visitElement(element: GroovyPsiElement) {
                    element.acceptChildren(this)
                }

                override fun onFound(kotlinPluginVersion: String, kotlinPluginStatement: GrCallExpression) {
                    version = kotlinPluginVersion
                }
            }
            gradleFile.accept(visitor)
            return version
        }
    }
}