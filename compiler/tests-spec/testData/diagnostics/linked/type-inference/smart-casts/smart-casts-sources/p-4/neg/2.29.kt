// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 29
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun <T : List<T>> Inv<out T>.case_1() {
    if (this is MutableList<*>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int"), DEBUG_INFO_IMPLICIT_RECEIVER_SMARTCAST!>this<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int"), DEBUG_INFO_IMPLICIT_RECEIVER_SMARTCAST!>this<!>[0] = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int"), DEBUG_INFO_IMPLICIT_RECEIVER_SMARTCAST!>this<!>[1]
    }
}