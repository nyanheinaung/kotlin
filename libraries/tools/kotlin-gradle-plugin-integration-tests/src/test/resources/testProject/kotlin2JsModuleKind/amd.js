/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

(function(global) {
    var modules = {};

    // Hard-code expected dependency order since we are unable to refer to modules by filename here.
    var names = ["kotlin", "app", "check"];

    function define(name, dependencies, body) {
        if (Array.isArray(name)) {
            body = dependencies;
            dependencies = name;
            name = names.shift();
        }
        else {
            if (name !== names.shift()) throw new Error("Unexpected dependency")
        }
        var resolvedDependencies = [];
        var currentModule = {};
        modules[name] = currentModule;
        for (var i = 0; i < dependencies.length; ++i) {
            var dependencyName = dependencies[i];
            resolvedDependencies[i] = dependencyName === 'exports' ? currentModule : modules[dependencyName];
        }
        currentModule = body.apply(body, resolvedDependencies);
        if (currentModule) {
            modules[name] = currentModule;
        }
    }
    define.amd = {};

    global.define = define;
})(this);