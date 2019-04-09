// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_VARIABLE -UNUSED_VALUE
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 33
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    val y = run {
        if (x is Class)
            return@run <!DEBUG_INFO_EXPRESSION_TYPE("Class & kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>
        Class()
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>.fun_1()
}

// TESTCASE NUMBER: 2
fun case_2(x: Class?) {
    val y = run {
        x!!
        return@run <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>.fun_1()
}

// TESTCASE NUMBER: 3
fun case_3(z: Any?) {
    val y = run {
        when (z) {
            is Class? -> <!DEBUG_INFO_SMARTCAST!>z<!>!!
            is Class -> return@run <!DEBUG_INFO_SMARTCAST!>z<!>
            is Float -> Class()
            else -> return@run Class()
        }
    }
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class")!>y<!>.fun_1()
}