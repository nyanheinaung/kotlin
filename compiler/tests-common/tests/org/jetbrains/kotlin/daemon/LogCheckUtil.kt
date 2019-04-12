/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon

/**
 * holder of a [regex] and optional [matchCheck] for additional checks on match result
 */
class LinePattern(val regex: Regex, val matchCheck: (MatchResult) -> Boolean = { true })
fun LinePattern(regex: String, matchCheck: (MatchResult) -> Boolean = { true }) = LinePattern(regex.toRegex(), matchCheck)

/**
 * calls [body] if receiver does not contain complete sequence of lines matched by [patternsIter], separated by any number of other lines
 * [body] receives first unmatched pattern and index of last matched line in the sequence
 */
fun Sequence<String>.ifNotContainsSequence(patternsIter: Iterator<LinePattern>,
                                                    body: (LinePattern, Int) -> Unit) : Unit {
    class Accumulator(it: Iterator<LinePattern>) {
        val iter = EndBoundIteratorWithValue(it)
        var lineNo = 1
        var lastMatchedLineNo = 0
        fun nextLineAndPattern(): Accumulator { iter.traverseNext(); lastMatchedLineNo = lineNo; return nextLine() }
        fun nextLine(): Accumulator { lineNo++; return this }
    }
    val res = fold(Accumulator(patternsIter))
                   { acc, line ->
                       when {
                           !acc.iter.isValid() -> return@fold acc
                           acc.iter.value.regex.find(line)?.let { acc.iter.value.matchCheck(it) } ?: false -> acc.nextLineAndPattern()
                           else -> acc.nextLine()
                       }
                   }
    if (res.iter.isValid()) {
        body(res.iter.value, res.lastMatchedLineNo)
    }
}


/**
 * calls [body] if receiver does not contain complete sequence of lines matched by [patterns], separated by any number of other lines
 * [body] receives first unmatched pattern and index of last matched line in the sequence
 */
fun Sequence<String>.ifNotContainsSequence(patterns: List<LinePattern>,
                                                    body: (LinePattern, Int) -> Unit): Unit {
    ifNotContainsSequence(patterns.iterator(), body)
}


/**
 * calls [body] if receiver does not contain complete sequence of lines matched by [patterns], separated by any number of other lines
 * [body] receives first unmatched pattern and index of last matched line in the sequence
 */
fun Sequence<String>.ifNotContainsSequence(vararg patterns: LinePattern,
                                                    body: (LinePattern, Int) -> Unit): Unit {
    ifNotContainsSequence(patterns.iterator(), body)
}


// emulates Stepanov's / STL iterator, but with "embedded" end check via isValid:
// iterator points to a current value and upon init points to the first element or is invalid
// allows to express some algorithms more concisely
private class EndBoundIteratorWithValue<T: Any, Iter: Iterator<T>>(val base: Iter) {
    private var _value: T? = base.nextOrNull()

    val value: T get() = _value ?: throw Exception("Dereferencing invalid iterator")

    fun isValid(): Boolean = _value != null

    fun traverseNext(): EndBoundIteratorWithValue<T, Iter> {
        _value = base.nextOrNull()
        return this
    }
}

private fun<T: Any> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null
