// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 45
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24901
 */
fun case_1(x: String?) {
    when {
        x == null -> return
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
}

// TESTCASE NUMBER: 2
fun case_2(x: String?) {
    when {
        x == null -> return
        else -> println(1)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24901
 */
fun case_3(x: String?) {
    when (x) {
        null -> return
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
}

// TESTCASE NUMBER: 4
fun case_4(x: String?) {
    when (x) {
        null -> return
        else -> println(1)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

// TESTCASE NUMBER: 5
fun case_5(x: String?) {
    when (x) {
        null -> throw Exception()
        else -> println(1)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

// TESTCASE NUMBER: 6
fun case_6(x: String?) {
    when (x) {
        null -> throw Exception()
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
}
