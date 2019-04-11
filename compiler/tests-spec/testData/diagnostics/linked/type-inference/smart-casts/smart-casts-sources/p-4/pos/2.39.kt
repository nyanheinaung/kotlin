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

// TESTCASE NUMBER: 1
fun case_1(x: Class?) {
    if (x != null) <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KFunction0<(kotlin.Int) -> (kotlin.Int) -> kotlin.Int>")!><!DEBUG_INFO_SMARTCAST!>x<!>::fun_1<!>
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-17386
 */
fun case_2(x: Class?) {
    val y = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KFunction0<(kotlin.Int) -> (kotlin.Int) -> kotlin.Int>?")!>if (x != null) x::fun_1 else null<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KFunction0<(kotlin.Int) -> (kotlin.Int) -> kotlin.Int>?")!>y<!>
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-17386
 */
fun case_3(x: Class?) {
    val y = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KFunction1<Class, (kotlin.Int) -> (kotlin.Int) -> kotlin.Int>?")!>if (x != null) Class::fun_1 else null<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.reflect.KFunction1<Class, (kotlin.Int) -> (kotlin.Int) -> kotlin.Int>?")!>y<!>
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-17386
 */
fun case_4(x: Class?) {
    val y = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>if (x != null) <!TYPE_MISMATCH!>x::fun_1<!> else 10<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>y<!>
}

/*
 * TESTCASE NUMBER: 5
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-17386
 */
fun case_5(x: Class?) {
    val y = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>if (x != null) <!TYPE_MISMATCH!>Class::fun_1<!> else 10<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>y<!>
}
