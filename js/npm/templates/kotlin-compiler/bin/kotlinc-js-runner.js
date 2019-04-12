/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

#!/usr/bin/env node
var spawn = require('child_process').spawn;

var execPath = __dirname + '/kotlinc-js';
var args = process.argv.slice(2);

spawn('"' + execPath + '"', args, { stdio: "inherit", shell: true }).on('exit', function(exitCode) {
    process.exit(exitCode);
});
