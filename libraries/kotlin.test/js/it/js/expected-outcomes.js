/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

module.exports = {
    'SimpleTest testFoo': 'fail',
    'SimpleTest testBar': 'pass',
    'SimpleTest testFooWrong': 'pending',
    'TestTest emptyTest': 'pending',
    'AsyncTest checkAsyncOrder': 'pass',
    'AsyncTest asyncPassing': 'pass',
    'AsyncTest asyncFailing': 'fail',
    'org OrgTest test': 'pass',
    'org.some SomeTest someIsBetterThanNothing': 'pass',
    'org.some.name NameTest test': 'pass',
    'org.other.name NameTest nameIsOk': 'pass'
};