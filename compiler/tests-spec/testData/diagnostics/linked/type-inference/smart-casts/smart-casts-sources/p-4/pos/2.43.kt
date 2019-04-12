// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER -UNUSED_VARIABLE -UNUSED_VALUE -VARIABLE_WITH_REDUNDANT_INITIALIZER
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 43
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1() {
    var x: String? = null

    outer@ while (x != null) {
        inner@ do {
            x = null
        } while (x == null)
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>x<!>.length
    }
}