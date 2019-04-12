/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.script.templates.standard

/**
 * Basic script definition template without parameters
 */
public abstract class SimpleScriptTemplate()

/**
 * Script definition template with standard argv-like parameter; default for regular kotlin scripts
 */
public abstract class ScriptTemplateWithArgs(val args: Array<String>)

/**
 * Script definition template with generic bindings parameter (String to Object)
 */
public abstract class ScriptTemplateWithBindings(val bindings: Map<String, Any?>)

