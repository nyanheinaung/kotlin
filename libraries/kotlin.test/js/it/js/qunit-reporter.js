/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

var Tester = require('./test-result-checker');

var full = require('./expected-outcomes');
var allAsyncPass = {};
for (var name in full) {
    allAsyncPass[name] = name.startsWith('AsyncTest ') ? 'pass' : full[name];
}

var tester = new Tester(allAsyncPass, 'qunit');

QUnit.testDone(function (details) {
    var testName = (details.module.replace('> ', '') + ' ' + details.name).trim();
    if (details.skipped) {
        tester.pending(testName);
    }
    else if (!details.failed) {
        tester.passed(testName);
    }
    else {
        tester.failed(testName);
    }
});

QUnit.done(function (details) {
    tester.printResult();
    details.failed = tester.exitCode();
});

