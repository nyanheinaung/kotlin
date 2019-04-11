// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 38
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * ISSUES: KT-10662
 */
fun case_1(x: Any) {
    if (x is String) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!><!DEBUG_INFO_SMARTCAST!>x<!><!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!><!>.length
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String")!>x<!>
    }
}

/*
 * TESTCASE NUMBER: 2
 * ISSUES: KT-10662
 */
fun case_2(x: Any?) {
    if (x is String?) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!><!DEBUG_INFO_SMARTCAST!>x<!>!!<!>.length
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any? & kotlin.String")!>x<!>
    }
}

/*
 * TESTCASE NUMBER: 3
 * ISSUES: KT-10662
 */
fun case_3(x: Any) {
    if (x is Map.Entry<*, *>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.collections.Map.Entry<kotlin.Any?, kotlin.Any?>")!><!DEBUG_INFO_SMARTCAST!>x<!><!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!><!>.<!UNRESOLVED_REFERENCE!>length<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.collections.Map.Entry<*, *>")!>x<!>
    }
}

/*
 * TESTCASE NUMBER: 4
 * ISSUES: KT-10662
 */
fun case_4(x: Any?) {
    if (x is Map.Entry<*, *>?) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.collections.Map.Entry<kotlin.Any?, kotlin.Any?>")!><!DEBUG_INFO_SMARTCAST!>x<!>!!<!>.<!UNRESOLVED_REFERENCE!>length<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any? & kotlin.collections.Map.Entry<*, *>")!>x<!>
    }
}