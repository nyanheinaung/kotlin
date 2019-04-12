// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 40
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_1() {
    val x: String? = null
    while (true) {
        println(x ?: break)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_2(y: MutableList<Int>) {
    val x: Int? = null
    while (true) {
        y[x ?: break] = 10
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_3(y: MutableList<Int>) {
    val x: Int? = null
    while (true) {
        y[0] = x ?: break
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

// TESTCASE NUMBER: 4
fun case_4() {
    val x: Int? = null
    while (true) {
        x ?: break
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
}

// TESTCASE NUMBER: 5
fun case_5(y: Boolean) {
    val x: Boolean? = null
    while (true) {
        y && (x ?: break)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!><!UNSAFE_CALL!>.<!>not()
}

// TESTCASE NUMBER: 6
fun case_6(y: Boolean) {
    val x: Boolean? = null
    while (true) {
        y || (x ?: break)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!><!UNSAFE_CALL!>.<!>not()
}

// TESTCASE NUMBER: 7
fun case_7(y: Boolean?) {
    val x: Boolean? = null
    while (true) {
        y ?: x ?: break
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean?")!>x<!><!UNSAFE_CALL!>.<!>not()
}

/*
 * TESTCASE NUMBER: 8
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_8() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        y += x ?: break
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

/*
 * TESTCASE NUMBER: 9
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_9() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        y -= x ?: break
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

/*
 * TESTCASE NUMBER: 10
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_10() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        val z = y - (x ?: break)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

/*
 * TESTCASE NUMBER: 11
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-24652
 */
fun case_11() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        val z = y * (x ?: break)
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
}

// TESTCASE NUMBER: 12
fun case_12() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        y += if (x == null) break else 10
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
}

// TESTCASE NUMBER: 13
fun case_13() {
    var y: Int = 10
    val x: Int? = null
    while (true) {
        val z = y * if (x != null) break else 10
    }
    <!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>inv()
}
