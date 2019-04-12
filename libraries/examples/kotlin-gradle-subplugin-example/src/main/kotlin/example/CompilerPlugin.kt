/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import com.intellij.mock.MockProject

public object ExampleConfigurationKeys {
    public val EXAMPLE_KEY: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("example argument")
}

public class ExampleCommandLineProcessor : CommandLineProcessor {
    companion object {
        public val EXAMPLE_PLUGIN_ID: String = "example.plugin"
        public val EXAMPLE_OPTION: CliOption = CliOption("exampleKey", "<value>", "")
    }

    override val pluginId: String = EXAMPLE_PLUGIN_ID
    override val pluginOptions: Collection<CliOption> = listOf(EXAMPLE_OPTION)

    override fun processOption(option: CliOption, value: String, configuration: CompilerConfiguration) {
        when (option) {
            EXAMPLE_OPTION -> configuration.put(ExampleConfigurationKeys.EXAMPLE_KEY, value)
            else -> throw CliOptionProcessingException("Unknown option: ${option.name}")
        }
    }
}

public class ExampleComponentRegistrar : ComponentRegistrar {
    public override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val exampleValue = configuration.get(ExampleConfigurationKeys.EXAMPLE_KEY)
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        messageCollector.report(CompilerMessageSeverity.INFO, "Project component registration: $exampleValue")
    }
}
