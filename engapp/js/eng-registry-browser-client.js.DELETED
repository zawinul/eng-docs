
function getRegistryClient(appName, appAuthentication, registryEndpoints) {
	
	if (!appName)
		appName ='engapp';
	
	if (!registryEndpoints)
		registryEndpoints = ['https://localhost:5432'];
	
	appName = appName || 'engapp';
	var registryClient = resilient({ service: { basePath: '/api/v1' } });
	registryClient.setServers(registryEndpoints);

	async function getThisConfig(forceRefresh) {
		if (getThisConfig.data && !forceRefresh)
			return getThisConfig.data;

		var configResponse = await registryClient.get('/app/config/' + appName, {
			headers: { Authentication: appAuthentication }
		});
		if (configResponse.status >= 300)
			return null;
		getThisConfig.data = configResponse.data; 
		return configResponse.data;
	}

	async function getServiceInfo(serviceName) {
		var infoResponse = await registryClient.get('/service/info/' + serviceName);
		var info = JSON.parse(infoResponse.body);
		return info;
	}


	async function getServiceClient(serviceName, basePath) {
		var sopt = typeof(basePath)=='string'
			? { basePath: basePath }
			: {};

		var resilientClient = resilient({
			service: sopt,
			balancer: {
				random: true,
				roundRobin: false
			},
			discover: {
				path:'/api/v1/service/endpoint/',
				refreshInterval: 15*1000
			}
		});
		var discoveryServers = registryEndpoints.map(x=>x+'/api/v1/service/endpoint/'+serviceName);
		resilientClient.discoveryServers(discoveryServers);

		function sendWithCredentials(method, resource, options) {
			return oidc.getIdToken()
			.then(function(token) { 
				return send(method, resource, options, token);
			});
		}

		
		function send(method, resource, options, token) {
			var h = token ? {
				method: method,
				dataType: 'json' 
			} : {
				method: method,
				dataType: 'json',
				headers: { Authorization: 'Bearer ' + token }
			};

			$.extend(h, options);
			var ajax =  resilientClient.send(resource, h);
			
			var processAjaxResults = ajax.then(
				function(response) {
					if (response.status<300)
						return Promise.resolve(response.data);
					else
						return Promise.reject(response);
				}, 
				function(catchData) {
					Promise.reject(catchData);
				}
			).then(data=> {
				try {
					if (h.dataType && h.dataType.toLowerCase()=='json')
						if (data && (typeof(data)=='string'))
							return JSON.parse(data);
				} catch(e) { console.log(e); }
				return data;
			});

			return processAjaxResults;
		}

		function get(risorsa, reqOptions) {
			return sendWithCredentials('GET', risorsa, reqOptions);
		}

		function getWithNoCredentials(risorsa, reqOptions) {
			return send('GET', risorsa, reqOptions);
		}

		function post(risorsa, reqOptions) {
			return sendWithCredentials('POST', risorsa, reqOptions);
		}

		function put(risorsa, reqOptions) {
			return sendWithCredentials('PUT', risorsa, reqOptions);
		}

		function _delete(risorsa, reqOptions) {
			return sendWithCredentials('DELETE', risorsa, reqOptions);
		}

		return {
			get: get,
			post: post,
			put: put,
			delete: _delete,

			getWithNoCredentials: getWithNoCredentials
		}
	}


	return {
		getThisConfig: getThisConfig,
		getServiceInfo: getServiceInfo,
		getServiceClient: getServiceClient
	};
}


function getThisRegistryClient(appName, appAuthentication, registryEndpoints) {
	if (!getThisRegistryClient.singleton)
		getThisRegistryClient.singleton = getRegistryClient(appName||'engapp'), appAuthentication, registryEndpoints;

	return getThisRegistryClient.singleton;
}



