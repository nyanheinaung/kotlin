// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 29
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29936
 */
fun case_1(x: Int?) {
    if (x != funNothingQuest() == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29936
 */
fun case_2(x: Int?) {
    operator fun Nothing?.not() = null
    if (x != !null != false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29936
 */
fun case_3(x: Int?) {
    if (x == funWithoutArgs() == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29936
 */
fun case_4(x: Int?, y: List<Nothing?>) {
    if (x == y[0] == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Int?) {
    val y = object {
        val z = null
    }

    if (x == y.z == true) {
        <!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

// TESTCASE NUMBER: 6
fun case_6(x: Int?) {
    val y = null

    if (x == <!DEBUG_INFO_CONSTANT!>y<!> == true) {
        <!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

// TESTCASE NUMBER: 7
fun case_7(x: Int?) {
    val y = object {
        val z: Nothing?
            get() = null
    }

    if (x == y.z == true) {
        <!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!>inv()
    }
}

/*
 * TESTCASE NUMBER: 8
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29936
 */
fun case_8(x: KClass<EmptyObject>?) {
    if (x == EmptyObject::class == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KClass<EmptyObject>?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KClass<EmptyObject>?")!>x<!><!UNSAFE_CALL!>.<!>java
    }
}
