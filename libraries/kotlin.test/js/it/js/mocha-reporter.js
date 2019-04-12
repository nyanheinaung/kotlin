/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

var mocha = require('mocha');
var Tester = require('./test-result-checker');
var expectedOutcomes = require('./expected-outcomes');

module.exports = function (runner) {
    mocha.reporters.Base.call(this, runner);

    var tester = new Tester(expectedOutcomes, 'mocha');

    runner.on('pass', function (test) {
        tester.passed(test.fullTitle().trim());
    });

    runner.on('fail', function (test, err) {
        tester.failed(test.fullTitle().trim());
    });

    runner.on('pending', function (test) {
        tester.pending(test.fullTitle().trim());
    });

    runner.on('end', function () {
        tester.end();
    });
};
