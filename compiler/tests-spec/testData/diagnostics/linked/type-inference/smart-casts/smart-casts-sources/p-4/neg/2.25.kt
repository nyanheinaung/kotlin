// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 25
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

/*
 * TESTCASE NUMBER: 1
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28370
 */
fun case_1() {
    var x: Boolean? = true
    if (x != null) {
        try {
            throw Exception()
        } catch (e: Exception) {
            x = null
        }
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.not()
    }
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28370
 */
fun case_2() {
    var x: Boolean? = true
    if (x != null) {
        try {
            x = null
        } finally { }
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.not()
    }
}

/*
 * TESTCASE NUMBER: 3
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29482
 */
fun case_3(x: String?) {
    while (true) {
        try {
            if (x == null) {
                throw Exception()
            }
        } catch (e: Exception) {
            break
        }
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29482
 */
fun case_4(x: String?) {
    while (true) {
        try {
            x!!
        } catch (e: Exception) {
            break
        }
    }
    x.length
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

/*
 * TESTCASE NUMBER: 5
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29482
 */
fun case_5(x: String?) {
    do {
        try {
            x!!
        } catch (e: Exception) {
            break
        }
    } while (true)
    x.length
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}

/*
 * TESTCASE NUMBER: 6
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-29482
 */
fun case_6(x: String?) {
    do {
        try {
            if (x == null) {
                throw Exception()
            }
        } catch (e: Exception) {
            break
        }
    } while (true)
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean & kotlin.Boolean?"), DEBUG_INFO_SMARTCAST!>x<!>.length
}
