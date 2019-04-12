/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

var amdModules = {};
// Hard-code expected dependency order since we are unable to refer to modules by filename here.
var moduleNames = ["kotlin", "test-js-moduleKind", "check"];
function define(moduleName, dependencies, body) {
    if (Array.isArray(moduleName)) {
        body = dependencies;
        dependencies = moduleName;
        moduleName = moduleNames.shift();
    }
    else {
        if (moduleName !== moduleNames.shift()) throw new Error("Unexpected dependency")
    }
    var resolvedDependencies = [];
    var currentModule = {};
    amdModules[moduleName] = currentModule;
    for (var i = 0; i < dependencies.length; ++i) {
        var dependencyName = dependencies[i];
        var dependency = dependencyName === 'exports' ? currentModule : amdModules[dependencyName];
        resolvedDependencies.push(dependency);
    }
    body.apply(body, resolvedDependencies);
}
define.amd = {};