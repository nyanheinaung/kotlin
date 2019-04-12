/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.constraintSystem

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.TypeResolver
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.resolve.constants.IntegerValueTypeConstructor
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import java.util.regex.Pattern

class ConstraintSystemTestData(
        context: BindingContext,
        private val project: Project,
        private val typeResolver: TypeResolver
) {
    private val functionFoo: FunctionDescriptor
    private val scopeToResolveTypeParameters: LexicalScope

    init {
        val functions = context.getSliceContents(BindingContext.FUNCTION)
        functionFoo = findFunctionByName(functions.values, "foo")
        val function = DescriptorToSourceUtils.descriptorToDeclaration(functionFoo) as KtFunction
        val fooBody = function.bodyExpression
        scopeToResolveTypeParameters = context.get(BindingContext.LEXICAL_SCOPE, fooBody)!!
    }

    private fun findFunctionByName(functions: Collection<FunctionDescriptor>, name: String): FunctionDescriptor {
        return functions.firstOrNull { it.name.asString() == name } ?:
               throw AssertionError("Function ${name} is not declared")
    }

    fun getParameterDescriptor(name: String): TypeParameterDescriptor {
        return functionFoo.typeParameters.firstOrNull { it.name.asString() == name } ?:
               throw AssertionError("Unsupported type parameter name: $name. You may add it to constraintSystem/declarations.kt")
    }

    fun getType(name: String): KotlinType {
        val matcher = INTEGER_VALUE_TYPE_PATTERN.matcher(name)
        if (matcher.find()) {
            val number = matcher.group(1)!!
            val parameters = CompileTimeConstant.Parameters(false, false, false, false, false, false, false)
            return KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
                Annotations.EMPTY,
                IntegerValueTypeConstructor(number.toLong(), functionFoo.module, parameters),
                listOf(),
                false,
                MemberScope.Empty
            )
        }
        return typeResolver.resolveType(
                scopeToResolveTypeParameters, KtPsiFactory(project).createType(name),
                KotlinTestUtils.DUMMY_TRACE, true)
    }
}

private val INTEGER_VALUE_TYPE_PATTERN = Pattern.compile("""IntegerValueType\((\d*)\)""")
