// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 47
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-16373
 */
class Case1<T> {
    fun getT(): T = TODO()
    fun getTN(): T? = null

    fun get(): T {
        var x = getTN()
        if (x == null) {
            x = getT()
        }
        <!DEBUG_INFO_EXPRESSION_TYPE("T?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("T?")!>x<!><!UNSAFE_CALL!>.<!>equals(10)
        return <!TYPE_MISMATCH, TYPE_MISMATCH!>x<!>
    }
}

// TESTCASE NUMBER: 2
class Case2 {
    fun getInt(): Int = 10
    fun getIntN(): Int? = null

    fun get(): Int {
        var x = getIntN()
        if (x == null) {
            x = getInt()
        }
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)
        return <!DEBUG_INFO_SMARTCAST!>x<!>
    }
}