(function(){

		
	engapp.rest = function () {
		function get(url, params, config) {
			return call('GET', url, params, config);
		}
	
		function post(url, params, body, config) {
			return call('POST', url, params, config);
		}
	
		function put(url, params, config) {
			return call('PUT', url, params, config);
		}
	
		function update(url, params, body, config) {
			return call('UPDATE', url, params, config);
		}
	
		function _delete(url, params, body, config) {
			return call('DELETE', url, params, config);
		}

		function call(method, url, params, _config) {
			var config = $.extend({}, _config);
			var ret = $.Deferred();
			oidc.getIdToken().then(function (id_token) {
				var c = $.extend({}, config || {});
				c.url = url;
				c.method = method;
				c.headers = c.headers || {};
	
				c.headers.Authorization = "Bearer "+id_token;
				
				c.error = c.error || function (jqXHR, textStatus) {
					console.log("Ajax Error, " + textStatus);
				};
				if (params)
					c.data = params;
	
				$.ajax(c).then(
					function (data, textStatus, jqXHR) {
						debugger;
						ret.resolve(data);
					},
					function (jqXHR, textStatus, errorThrown) {
						debugger;
						console.log({ restError: { url: c.url, jqXHR: jqXHR } });
						ret.reject(jqXHR, textStatus, errorThrown)
					}
				);
			});
			return ret;
		}

		function getUnauthenticated(url, params, config) {
			var ret = $.Deferred();
			var c = $.extend({}, config || {});
			c.url = url;
			c.method = 'GET';
			c.headers = c.headers || {};
			c.error = c.error || function (jqXHR, textStatus) {
				console.log("Ajax Error, " + textStatus);
			};
			if (params)
				c.data = params;
	
			$.ajax(c).then(
				function (data, textStatus, jqXHR) {
					debugger;
					ret.resolve(data);
				},
				function (jqXHR, textStatus, errorThrown) {
					debugger;
					console.log({ restError: { url: c.url, jqXHR: jqXHR } });
					ret.reject(jqXHR, textStatus, errorThrown)
				}
			);
			return ret;	
		}
	
		return {
			call: call,
			get: get,
			post: post,
			update: update,
			delete: _delete,
			put: put,
			getUnauthenticated: getUnauthenticated
		}
	}();
		

	// engapp.restService = function(addrList, config) {
	// 	var userHelper;
	
	// 	var defaultConfig = { 
	// 		service: { basePath: '/' },
	// 		balancer: {
	// 			random: true,
	// 			roundRobin: false
	// 		}
	// 	};
	// 	if (config)
	// 		$.extend(defaultConfig, config);
	
	// 	var client = resilient(defaultConfig);
	// 	client.setServers(addrList);
	
	// 	client.useHttpClient(function httpProxy(options, cb) {
	// 		options = userHelper.preInvio(options);
	// 		//options.jsonp= "callback";
	// 		//options.dataType= "jsonp";
	// 		options.success = function (data, status, xhr) {
	// 			cb(null, { status: xhr.status, data: data, xhr: xhr })
	// 		};
	
	// 		options.error = function (xhr) {
	// 			cb({ status: xhr.status, xhr: xhr })
	// 		};
	
	// 		options.headers = options.headers || {};
	// 		// ... options.headers.Authorization = "Bearer " + token;
	
	// 		$.ajax(options)
	// 	});
	
	// 	function preInvio(options) {
	// 		return options;
	// 	}
	
	// 	function get(risorsa, parametri, options) {
	// 		options = options || {};
	// 		if (parametri)
	// 			options.data = parametri;
	// 		return client.get(risorsa, options).then(resp=>resp.data);
	// 	}
	
	// 	function post(risorsa, parametri, body, opzioni) {
	
	// 	}
	
	// 	userHelper =  {
	// 		preInvio:preInvio,
	// 		get: get,
	// 		post: post
	// 		// etc...
	// 	};
	// 	return userHelper;
	
	// };
	
	
	
	

})();
	
