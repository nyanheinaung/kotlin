/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

var Tester = require('./test-result-checker');
var expectedOutcomes = require('./expected-outcomes');

module.exports = function (results) {
    var tester = new Tester(expectedOutcomes, 'jest');
    var testResults = results.testResults[0].testResults;
    for (var i = 0; i < testResults.length; i++) {
        var tr = testResults[i];

        var name = tr.fullName.trim();
        if (tr.status === 'passed') {
            tester.passed(name);
        }
        else if (tr.status === 'failed') {
            tester.failed(name);
        }
        else {
            tester.pending(name);
        }
    }
    tester.end();
};