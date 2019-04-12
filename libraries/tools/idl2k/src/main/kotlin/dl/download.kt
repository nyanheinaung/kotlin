/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.idl2k.dl

import org.jetbrains.idl2k.urls
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import java.io.File
import java.net.URL

fun main(args: Array<String>) {
    val dir = File("../../stdlib/js/idl")
    dir.mkdirs()

    var packageFilter: String? = null
    val argsIterator = args.iterator()
    while (argsIterator.hasNext()) {
        val arg = argsIterator.next()

        when (arg) {
            "--pkg" -> if (argsIterator.hasNext()) packageFilter = argsIterator.next() else throw IllegalArgumentException("argument $arg requires argument")
            else -> throw IllegalArgumentException("Argument $arg is unknown")
        }
    }

    val urlsPerFiles = urls.filter { packageFilter == null || it.second == packageFilter }.groupBy { it.second + ".idl" }

    urlsPerFiles.forEach { e ->
        val fileName = e.key
        val pkg = e.value.first().second

        File(dir, fileName).bufferedWriter().use { w ->
            w.appendln("namespace $pkg;")
            w.appendln()
            w.appendln()

            e.value.forEach { (url) ->
                println("Loading $url...")

                w.appendln("// Downloaded from $url")
                val content = fetch(url)

                if (content != null) {
                    if (url.endsWith(".idl")) {
                        w.appendln(content)
                    } else {
                        extractIDLText(content, w)
                    }
                }
            }

            w.appendln()
        }
    }
}

private fun fetch(url: String): String? {
    try {
        return URL(url).readText()
    } catch (e: Exception) {
        println("failed to download ${url}, if it's not a local problem, revisit the list of downloaded entities")
        e.printStackTrace()
        return null
    }
}

private fun Appendable.append(element: Element) {
    val text = element.text()
    appendln(text)
    if (!text.trimEnd().endsWith(";")) {
        appendln(";")
    }
}


private fun List<Element>.attachTo(out: Appendable) = map { element ->
    if (!element.tag().preserveWhitespace()) {
        Element(Tag.valueOf("pre"), element.baseUri()).appendChild(element)
    } else element
}.forEach { out.append(it) }


private fun extractIDLText(rawContent: String, out: Appendable) {
    val soup = Jsoup.parse(rawContent)

    soup.select(".dfn-panel").remove()

    soup.select("pre.idl").filter {!it.hasClass("extract")}.attachTo(out)
    soup.select("code.idl-code").attachTo(out)
    soup.select("spec-idl").attachTo(out)
}
