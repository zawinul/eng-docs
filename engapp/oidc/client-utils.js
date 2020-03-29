var oidc = (function () {
	var listenerMap = {};

	var user;
	var accessToken;
	var idToken;
	var expire;

	var credentials;

	var appName = 'engapp';

	function setAppName(n) {
		appName = n;
	}

	function logout() {
		credentials = null;
		location.href = "https://oidc-provider:3043/session/end?cb="+escape(location.href);
	}

	function initMessageListener() {
		if (initMessageListener.messageListenerInitialized)
			return;

		function onEvent(ev) {
			console.log({ event: ev });
			var j = ev.data;
			var d = JSON.parse(ev.data);
			if (d.msg == "onOidcData") {
				var cb = listenerMap[d.listenerId];
				if (cb) {
					cb(d.payload);
					delete listenerMap[d.listenerId];
				}
			}
		};

		if (window.addEventListener)
			window.addEventListener("message", onEvent);
		else
			window.attachEvent("onmessage", onEvent);

		initMessageListener.messageListenerInitialized = true;
	};

	function tokenIsValid() {
		return (new Date().getTime() < credentials.exp*1000);
	}

	function isLogged() {
		if (!credentials && localStorage.engSingleSignOnData) 
			credentials = JSON.parse(localStorage.engSingleSignOnData);
		return !!credentials && tokenIsValid();
	}

	function getCredentials(samePage) {
		if (isLogged())
			return Promise.resolve(credentials);

		if (getCredentials.promise)
			return getCredentials.promise;

		var ret = getCredentials.promise = $.Deferred();
		const mark = Math.floor(Math.random() * 1000000000);
		const nonce = Math.floor(Math.random() * 1000000000);
		const hsecret = Math.floor(Math.random() * 1000000000);
		const hmark = mark ^ hsecret;
		const hnonce = nonce ^ hsecret;

		if (samePage) {
			localStorage.hmark = "" + hmark;
			localStorage.hnonce = "" + hnonce;
			localStorage.hsecret = "" + hsecret;
			var restart = location.href;

			var params = { restartFrom: restart, mark: mark };
			var u = urlForImplicitSingleSignOn(appName, params, nonce, location.origin + "/oidc/cb.html");
			location.href = u;
		}
		else {
			initMessageListener();
			var listenerId = "" + Math.random();
			var params = { dataToParent: true, listenerId: listenerId, mark: mark };
			var u = urlForImplicitSingleSignOn(appName, params, nonce, location.origin + "/oidc/cb.html");
			var w;
			listenerMap[listenerId] = function (d) {
				ret.resolve(d);
				credentials = d;
				localStorage.engSingleSignOnData = JSON.stringify(d, null, 2);
				setTimeout(function () {
					w.close();
				}, 100000);
			}
			w = window.open(u, "finestra-oidc");
		}

		return ret;
	}

	// function _get() {
	// 	//console.log('_get '+tokenIsValid()+' '+!!singleWait);

	// 	if (!tokenIsValid() && singleWait)
	// 		return singleWait;

	// 	singleWait = $.Deferred();
	// 	old_getCredentials().then(function (oidc) {
	// 		camundaId = oidc.user.camunda_id;
	// 		accessToken = oidc.access_token;
	// 		debugger;
	// 		idToken = oidc.idToken;
	// 		user = oidc.user;
	// 		expire = (new Date(oidc.exp * 1000)).getTime();
	// 		valid = true;
	// 		//console.log('singlewait resolve');
	// 		singleWait.resolve(oidc);
	// 	});

	// 	return singleWait;
	// }

	// function old_getCredentials(samePage) {
	// 	if (localStorage.engSingleSignOnData) {
	// 		var x = JSON.parse(localStorage.engSingleSignOnData);
	// 		var exp = new Date(x.exp * 1000);
	// 		var now = new Date();
	// 		if (now.getTime() < exp.getTime()) {
	// 			window.me = x;
	// 			return Promise.resolve(x);
	// 		}
	// 	}

	// 	var ret = $.Deferred();

	// 	const mark = Math.floor(Math.random() * 1000000000);
	// 	const nonce = Math.floor(Math.random() * 1000000000);
	// 	const hsecret = Math.floor(Math.random() * 1000000000);
	// 	const hmark = mark ^ hsecret;
	// 	const hnonce = nonce ^ hsecret;

	// 	if (samePage) {
	// 		localStorage.hmark = "" + hmark;
	// 		localStorage.hnonce = "" + hnonce;
	// 		localStorage.hsecret = "" + hsecret;
	// 		var restart = location.href;

	// 		var params = { restartFrom: restart, mark: mark };
	// 		var u = urlForImplicitSingleSignOn(appName, params, nonce, location.origin + "/oidc/cb.html");
	// 		location.href = u;
	// 	}
	// 	else {
	// 		initMessageListener();
	// 		var listenerId = "" + Math.random();
	// 		var params = { dataToParent: true, listenerId: listenerId, mark: mark };
	// 		var u = urlForImplicitSingleSignOn(appName, params, nonce, location.origin + "/oidc/cb.html");
	// 		var w;
	// 		listenerMap[listenerId] = function (d) {
	// 			ret.resolve(d);

	// 			localStorage.engSingleSignOnData = JSON.stringify(d, null, 2);
	// 			setTimeout(function () {
	// 				w.close();
	// 			}, 100);
	// 		}
	// 		w = window.open(u, "finestra-oidc");
	// 	}

	// 	return ret;
	// }

	function urlForImplicitSingleSignOn(client, callerParams, nonce, cbURI) {

		var enc_state = btoa(JSON.stringify(callerParams));
		var params = {
			response_type: 'id_token token',
			state: enc_state,
			nonce: nonce,
			client_id: client,
			scope: 'openid email profile',
			//scope: 'openid',
			redirect_uri: cbURI
		};
		var a = [];
		for (var k in params)
			a.push(k + '=' + escape(params[k]));
		var url = 'https://oidc-provider:3043/auth?' + a.join('&');
		return url;
	}

	function getUser_OLD() {
		return getCredentials().then(function(){ return credentials.user;})
	}

	function getUser() {
		return getAccessToken().then(function(at) {
			var get = $.ajax('https://oidc-provider:3043/me',{
				method:'GET',
				dataType: 'json', 
				headers: {
					Authorization: 'Bearer '+at
				}
			});
		
			get.catch(function(error) {
				ret.reject(error);
				console.log("getUserInfo error", error);
			});
			return get;
		});
	}

	function getAccessToken() {
		return getCredentials().then(function(){ return credentials.access_token;})
	}

	function getIdToken() {
		return getCredentials().then(function(){ return credentials.id_token;})
	}

	if (localStorage.engSingleSignOnData) {
		var x = JSON.parse(localStorage.engSingleSignOnData);
		window.me = x;
	}


	return {
		isLogged: isLogged,
		getUser: getUser,
		getAccessToken: getAccessToken,
		getIdToken: getIdToken,
		getCredentials: getCredentials,
		logout: logout,
		setAppName: setAppName
	};

})();

function getUrlParams(url) {
	var ret = {};
	if (url.indexOf('?') < 0)
		return ret;

	url.split('?')[1].split('&').map(function (x) {
		var y = x.split('=');
		ret[y[0]] = unescape(y[1]);
	});
	return ret;
}

var ajax = function () {
	function get(url, params, config) {
		return call('GET', url, params, config);
	}

	function post(url, params, body, config) {

	}

	function call(method, url, params, _config) {
		var config = $.extend({}, _config);
		var ret = $.Deferred();
		oidc.getAccessToken().then(function (access_token) {
			var c = $.extend({}, config || {});
			c.url = url;
			c.method = method;
			c.headers = c.headers || {};
			c.headers.Authorization = "Bearer " + access_token;
			c.error = c.error || function (jqXHR, textStatus) {
				console.log("Ajax Error, " + textStatus);
			};
			if (params)
				c.data = params;

			$.ajax(c).then(
				function (data, textStatus, jqXHR) {
					ret.resolve(data);
				},
				function (jqXHR, textStatus, errorThrown) {
					console.log({ ajaxError: { url: c.url, jqXHR: jqXHR } });
					ret.reject(jqXHR, textStatus, errorThrown)
				}
			);
		});
		return ret;
	}
	// etc...

	return {
		get: get,
		post: post
		// etc...
	}
}();

function restService(addrList) {
	var token;
	var client = resilient({ 
		service: { basePath: '/' },
		balancer: {
			random: true,
			roundRobin: false
		}
	});
	client.setServers(addrList);
	client.useHttpClient(function httpProxy(options, cb) {
		options.success = function (data, status, xhr) {
			cb(null, { status: xhr.status, data: data, xhr: xhr })
		};
		options.error = function (xhr) {
			cb({ status: xhr.status, xhr: xhr })
		};
		options.headers = options.headers || {};
		options.headers.Authorization = "Bearer " + token;

		$.ajax(options)
	})

	function get(risorsa, parametri) {
		return oidc.getAccessToken().then(function (access_token) {
			token = access_token;
			return client.get(risorsa);
		});
	}

	function post(risorsa, parametri, body, opzioni) {

	}

	return {
		get: get,
		post: post
		// etc...
	}

}