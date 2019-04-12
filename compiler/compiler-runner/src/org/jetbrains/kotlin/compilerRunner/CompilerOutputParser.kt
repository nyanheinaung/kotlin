/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner

import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.containers.Stack
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollectorUtil.reportException
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.io.Reader
import javax.xml.parsers.SAXParserFactory

object CompilerOutputParser {
    fun parseCompilerMessagesFromReader(messageCollector: MessageCollector, reader: Reader, collector: OutputItemsCollector) {
        // Sometimes the compiler doesn't output valid XML.
        // Example: error in command line arguments passed to the compiler.
        // The compiler will print the usage and the SAX parser will break.
        // In this case, we want to read everything from this stream and report it as an IDE error.
        val stringBuilder = StringBuilder()
        val wrappingReader = object : Reader() {
            @Throws(IOException::class)
            override fun read(cbuf: CharArray, off: Int, len: Int): Int {
                val read = reader.read(cbuf, off, len)
                stringBuilder.append(cbuf, off, len)
                return read
            }

            @Throws(IOException::class)
            override fun close() {
                // Do nothing:
                // If the SAX parser sees a syntax error, it throws an exception
                // and calls close() on the reader.
                // We prevent the reader from being closed here, and close it later,
                // when all the text is read from it
            }
        }
        try {
            val factory = SAXParserFactory.newInstance()
            val parser = factory.newSAXParser()
            parser.parse(InputSource(wrappingReader), CompilerOutputSAXHandler(messageCollector, collector))
        }
        catch (e: Throwable) {
            // Load all the text into the stringBuilder
            try {
                // This will not close the reader (see the wrapper above)
                FileUtil.loadTextAndClose(wrappingReader)
            }
            catch (ioException: IOException) {
                reportException(messageCollector, ioException)
            }

            val message = stringBuilder.toString()
            reportException(messageCollector, IllegalStateException(message, e))
            messageCollector.report(ERROR, message)
        }
        finally {
            try {
                reader.close()
            }
            catch (e: IOException) {
                reportException(messageCollector, e)
            }
        }
    }

    private class CompilerOutputSAXHandler(private val messageCollector: MessageCollector, private val collector: OutputItemsCollector) : DefaultHandler() {

        private val message = StringBuilder()
        private val tags = Stack<String>()
        private var path: String? = null
        private var line: Int = 0
        private var column: Int = 0

        @Throws(SAXException::class)
        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            tags.push(qName)

            message.setLength(0)

            path = attributes.getValue("path")
            line = safeParseInt(attributes.getValue("line"), -1)
            column = safeParseInt(attributes.getValue("column"), -1)
        }

        @Throws(SAXException::class)
        override fun characters(ch: CharArray?, start: Int, length: Int) {
            if (tags.size == 1) {
                // We're directly inside the root tag: <MESSAGES>
                val message = String(ch!!, start, length)
                if (!message.trim { it <= ' ' }.isEmpty()) {
                    messageCollector.report(ERROR, "Unhandled compiler output: $message")
                }
            }
            else {
                message.append(ch, start, length)
            }
        }

        @Throws(SAXException::class)
        override fun endElement(uri: String?, localName: String, qName: String) {
            if (tags.size == 1) {
                // We're directly inside the root tag: <MESSAGES>
                return
            }
            val qNameLowerCase = qName.toLowerCase()
            var category: CompilerMessageSeverity? = CATEGORIES[qNameLowerCase]
            if (category == null) {
                messageCollector.report(ERROR, "Unknown compiler message tag: $qName")
                category = INFO
            }
            val text = message.toString()

            if (category == OUTPUT) {
                reportToCollector(text)
            }
            else {
                messageCollector.report(category, text, CompilerMessageLocation.create(path, line, column, null))
            }
            tags.pop()
        }

        private fun reportToCollector(text: String) {
            val output = OutputMessageUtil.parseOutputMessage(text)
            if (output != null) {
                collector.add(output.sourceFiles, output.outputFile)
            }
        }

        companion object {
            private val CATEGORIES = mapOf(
                    "error" to ERROR,
                    "warning" to WARNING,
                    "logging" to LOGGING,
                    "output" to OUTPUT,
                    "exception" to EXCEPTION,
                    "info" to INFO,
                    "messages" to INFO)

            private fun safeParseInt(value: String?, defaultValue: Int): Int {
                if (value == null) {
                    return defaultValue
                }
                try {
                    return Integer.parseInt(value.trim { it <= ' ' })
                }
                catch (e: NumberFormatException) {
                    return defaultValue
                }

            }
        }
    }
}
