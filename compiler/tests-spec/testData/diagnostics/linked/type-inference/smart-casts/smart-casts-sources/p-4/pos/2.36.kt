// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 36
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Int) = ""
fun case_1(x: Int?) = 10
fun case_1() {
    var x: Int? = 10
    if (x != null) {
        val z = case_1(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>z<!>
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Int) = ""
fun case_2(x: Int?) = 10
fun case_2() {
    var x: Int? = 10
    var y = { x = null }
    if (x != null) {
        val z = case_2(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 3
val case_3_prop: Int?
    get() = 10
fun case_3(x: Int) = ""
fun case_3(x: Int?) = 10
fun case_3() {
    if (case_3_prop != null) {
        val z = case_3(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>case_3_prop<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 4
class Case4 {
    var x: Int? = 10
}
fun case_4(x: Int) = ""
fun case_4(x: Int?) = 10
fun case_4(y: Case4) {
    if (y.x != null) {
        val z = case_4(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>y.x<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 5
open class Case5 {
    open val x: Int? = 10
}
fun case_5(x: Int) = ""
fun case_5(x: Int?) = 10
fun case_5(y: Case4) {
    if (y.x != null) {
        val z = case_5(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>y.x<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 6
class Case6 {
    val x: Int? by lazy { 10 }
}
fun case_6(x: Int) = ""
fun case_6(x: Int?) = 10
fun case_6(y: Case4) {
    if (y.x != null) {
        val z = case_6(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>y.x<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 7
var case_7_prop: Int?
    get() = 10
    set() {}
fun case_7(x: Int) = ""
fun case_7(x: Int?) = 10
fun case_7() {
    if (case_7_prop != null) {
        val z = case_7(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>case_7_prop<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}

// TESTCASE NUMBER: 7
var case_7_prop: Int?
    get() = 10
    set() {}
fun case_7(x: Int) = ""
fun case_7(x: Int?) = 10
fun case_7() {
    if (case_7_prop != null) {
        val z = case_7(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>case_7_prop<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>z<!>
    }
}