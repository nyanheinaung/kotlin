/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

var Tester = require('./test-result-checker');
var tester = new Tester(require('./expected-outcomes'), 'jasmine');

process.on('exit', function() {
    tester.end();
});

jasmine.getEnv().addReporter({
    specDone: function(result) {
        var status = result.status;
        var name = result.fullName.trim();
        if (status === 'passed') {
            tester.passed(name);
        }
        else if (status === 'failed') {
            tester.failed(name);
        }
        else {
            tester.pending(name);
        }
    }
});