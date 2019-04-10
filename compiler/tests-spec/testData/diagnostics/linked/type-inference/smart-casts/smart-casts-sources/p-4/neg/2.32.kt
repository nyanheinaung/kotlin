// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 32
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Interface1) = x
fun case_1(x: Interface2) = x
fun case_1() {
    val x: Interface1 = null <!CAST_NEVER_SUCCEEDS!>as<!> Interface1
    x as Interface2
    <!OVERLOAD_RESOLUTION_AMBIGUITY!>case_1<!>(<!DEBUG_INFO_EXPRESSION_TYPE("Interface1 & Interface2")!>x<!>)
}
