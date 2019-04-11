// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 37
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_1(x: Class?, y: Any) {
    x?.prop_12 = if (y is String) "" else throw Exception()
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}

// TESTCASE NUMBER: 2
fun case_2(x: Class?, y: Any) {
    x?.prop_9 = y is String || return
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>y<!>.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>toUpperCase<!>()
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_3(x: Class?, y: Any) {
    x?.prop_12 = y as String
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_4(x: Class?, y: Any) {
    x?.prop_12 = y as? String ?: return
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.String"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}

/*
 * TESTCASE NUMBER: 5
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_5(x: Class?, y: String?) {
    x?.prop_12 = y ?: return
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}

// TESTCASE NUMBER: 6
fun case_6(x: Class?, y: String?) {
    x?.prop_9 = y !is String && throw Exception()
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String?")!>y<!><!UNSAFE_CALL!>.<!>toUpperCase()
}

/*
 * TESTCASE NUMBER: 7
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_7(x: Class?, y: String?) {
    x?.prop_12 = y!!
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}

/*
 * TESTCASE NUMBER: 8
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-13650
 */
fun case_8(x: Class?, y: String?) {
    x?.prop_12 = if (y === null) throw Exception() else ""
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String & kotlin.String?"), DEBUG_INFO_SMARTCAST!>y<!>.toUpperCase()
}
