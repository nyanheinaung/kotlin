// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 34
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1, 2, 3, 4, 5
fun nullableStringArg(number: String?) {}

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25453
 */
fun case_1(x: Int?) {
    if (x == null) {
        nullableStringArg(<!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>)
    }
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25453
 */
fun case_2(x: Int?, y: Nothing?) {
    if (x == <!DEBUG_INFO_CONSTANT!>y<!>) {
        nullableStringArg(<!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>)
    }
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25453
 */
fun case_3(x: Int?) {
    if (x == null) {
        nullableStringArg(<!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>)
    }
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25453
 */
fun case_4(x: Int?) {
    if (x == null) {
        nullableStringArg(<!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>x<!>)
    }
}

/*
 * TESTCASE NUMBER: 5
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25453
 */
fun case_5(x: Int?) {
    if (x == null) {
        var y = <!DEBUG_INFO_CONSTANT!>x<!>
        nullableStringArg(<!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Nothing?")!>y<!>)
    }
}
