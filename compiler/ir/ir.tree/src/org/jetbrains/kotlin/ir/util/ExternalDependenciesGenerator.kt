/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import kotlin.math.min

class ExternalDependenciesGenerator(
    moduleDescriptor: ModuleDescriptor,
    val symbolTable: SymbolTable,
    val irBuiltIns: IrBuiltIns,
    externalDeclarationOrigin: ((DeclarationDescriptor) -> IrDeclarationOrigin)? = null,
    private val deserializer: IrDeserializer? = null
) {
    private val stubGenerator = DeclarationStubGenerator(
        moduleDescriptor, symbolTable, irBuiltIns.languageVersionSettings, externalDeclarationOrigin, deserializer
    )

    fun generateUnboundSymbolsAsDependencies() {
        stubGenerator.unboundSymbolGeneration = true
        ArrayList(symbolTable.unboundClasses).forEach {
            stubGenerator.generateClassStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundConstructors).forEach {
            stubGenerator.generateConstructorStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundEnumEntries).forEach {
            stubGenerator.generateEnumEntryStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundFields).forEach {
            stubGenerator.generateFieldStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundSimpleFunctions).forEach {
            stubGenerator.generateFunctionStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundProperties).forEach {
            stubGenerator.generatePropertyStub(it.descriptor)
        }
        ArrayList(symbolTable.unboundTypeParameters).forEach {
            stubGenerator.generateOrGetTypeParameterStub(it.descriptor)
        }

        deserializer?.declareForwardDeclarations()

        assertEmpty(symbolTable.unboundClasses, "classes")
        assertEmpty(symbolTable.unboundConstructors, "constructors")
        assertEmpty(symbolTable.unboundEnumEntries, "enum entries")
        assertEmpty(symbolTable.unboundFields, "fields")
        assertEmpty(symbolTable.unboundSimpleFunctions, "simple functions")
        assertEmpty(symbolTable.unboundProperties, "properties")
        assertEmpty(symbolTable.unboundTypeParameters, "type parameters")
    }

    private fun assertEmpty(s: Set<IrSymbol>, marker: String) {
        assert(s.isEmpty()) {
            "$marker: ${s.size} unbound:\n" +
                    s.toList().subList(0, min(10, s.size)).joinToString(separator = "\n") { it.descriptor.toString() }
        }
    }
}