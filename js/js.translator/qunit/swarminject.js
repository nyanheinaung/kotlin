/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed 
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// load testswarm agent
(function() {
    var url = window.location.search;
	url = decodeURIComponent( url.slice( url.indexOf("swarmURL=") + 9 ) );
	if ( !url || url.indexOf("http") !== 0 ) {
		return;
	}
    document.write("<scr" + "ipt src='http://swarm.jquery.org/js/inject.js?" + (new Date).getTime() + "'></scr" + "ipt>");
})();
