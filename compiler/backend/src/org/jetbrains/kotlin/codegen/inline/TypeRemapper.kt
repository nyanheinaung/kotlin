/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import java.util.*

class TypeParameter(val oldName: String, val newName: String?, val isReified: Boolean, val signature: String?)

//typeMapping data could be changed outside through method processing
class TypeRemapper private constructor(
    private val typeMapping: MutableMap<String, String?>,
    val parent: TypeRemapper? = null,
    private val isRootInlineLambda: Boolean = false
) {
    private val additionalMappings = hashMapOf<String, String>()
    private val typeParametersMapping = hashMapOf<String, TypeParameter>()

    fun addMapping(type: String, newType: String) {
        typeMapping.put(type, newType)
    }

    fun hasNoAdditionalMapping(type: String): Boolean {
        return typeMapping.containsKey(type)
    }

    fun map(type: String): String {
        return typeMapping[type] ?: additionalMappings[type] ?: type
    }

    fun addAdditionalMappings(oldName: String, newName: String) {
        additionalMappings[oldName] = newName
    }

    fun registerTypeParameter(name: String) {
        assert(typeParametersMapping[name] == null) {
            "Type parameter already registered $name"
        }
        typeParametersMapping[name] = TypeParameter(name, name, false, null)
    }

    fun registerTypeParameter(mapping: TypeParameterMapping) {
        typeParametersMapping[mapping.name] = TypeParameter(
            mapping.name, mapping.reificationArgument?.parameterName, mapping.isReified, mapping.signature
        )
    }

    fun mapTypeParameter(name: String): TypeParameter? {
        return typeParametersMapping[name] ?: if (!isRootInlineLambda) parent?.mapTypeParameter(name) else null
    }

    companion object {
        @JvmStatic
        fun createRoot(formalTypeParameters: TypeParameterMappings?): TypeRemapper {
            return TypeRemapper(HashMap<String, String?>()).apply {
                formalTypeParameters?.forEach {
                    registerTypeParameter(it)
                }
            }
        }

        @JvmStatic
        fun createFrom(mappings: MutableMap<String, String?>): TypeRemapper {
            return TypeRemapper(mappings)
        }

        @JvmStatic
        @JvmOverloads
        fun createFrom(parentRemapper: TypeRemapper, mappings: Map<String, String?>, isRootInlineLambda: Boolean = false): TypeRemapper {
            return TypeRemapper(createNewAndMerge(parentRemapper, mappings), parentRemapper, isRootInlineLambda)
        }

        private fun createNewAndMerge(remapper: TypeRemapper, additionalTypeMappings: Map<String, String?>): MutableMap<String, String?> {
            return HashMap(remapper.typeMapping).apply {
                this += additionalTypeMappings
            }
        }
    }
}
