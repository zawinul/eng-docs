const Resilient = require('resilient');

function parseData(x) {
	if (typeof(x)=='string') {
		var j = x.trim().substring(0,1);
		if (j=='{' || j=='[')
			return JSON.parse(x);
	}
	return x;
}

async function create(engRegisterEndpoints) {

	if (!engRegisterEndpoints)
		engRegisterEndpoints = ['https://127.0.0.1:5432'];

	else if (typeof(engRegisterEndpoints)=='string')
		engRegisterEndpoints = [engRegisterEndpoints];

	var client = Resilient({ service: { basePath: '/api/v1/' }})
	client.setServers(engRegisterEndpoints);		

	try {
		var x = await client.get('alive');		
		console.log(x.data);
	} catch (error) {
		console.log(error);
		return null;
	}

	async function getServiceConfig(serviceName, serviceAuthorization) {
		var response = await client.get('service/config/'+serviceName, {
			headers:{ Authorization: serviceAuthorization}
		});
		if (response.status>=300)
			return null;
		return parseData(response.body);
	}
	

	async function setServiceConfig(serviceName, configData, serviceAuthorization) {
		if (typeof(configData)!='string')
			infoData = JSON.stringify(configData, null,2);
		var response = await client.put('service/config/' + serviceName, {
			headers: { 
				Authorization: serviceAuthorization,
				"Content-Type": "application/json"
			},
			data: configData
		});
		return parseData(response.body);
	}
	

	async function getServiceInfo(serviceName) {
		var infoResponse = await client.get('service/info/' + serviceName);
		return parseData(infoResponse.body);
	}
	
	async function setServiceInfo(serviceName, infoData, serviceAuthorization) {
		if (typeof(infoData)!='string')
			infoData = JSON.stringify(infoData, null,2);
		var response = await client.put('service/info/' + serviceName, {
			headers: { 
				Authorization: serviceAuthorization,
				"Content-Type": "application/json"
			},
			data: infoData
		});
		return parseData(response.body);
	}
	
	async function registerServiceEndpoint(serviceName, url, expire, serviceAuthorization) {
		var data = 	 {
			name: serviceName,
			url: url,
			expire: expire
		};

		try {
			var response = await client.post('service/endpoint/'+serviceName, {
				headers: { 
					Authorization: serviceAuthorization,
					"Content-Type": "application/json"
				},
				data: JSON.stringify(data)	
			});
			return parseData(response.body);
	
		}
		catch(e) {
			console.log(e);
			return null;
		}
	}
	
	
	function createService(serviceName, serviceAuthorization) {
		var serviceRegisterInterval;
	
		async function getThisConfig() {
			return await getServiceConfig(serviceName, serviceAuthorization);
		}
		
		async function setMyInfo(infoData) {
			return await setServiceInfo(serviceName, infoData, serviceAuthorization);
		}
		
		async function getThisInfo() {
			return await getServiceInfo(serviceName);
		}
		
		async function getServiceInfo(serviceName) {
			return await getServiceInfo(serviceName);
		}
	
		async function startRegisterEndpointTimer(serviceUrl, msRefresh) {

			if (!msRefresh) 
				msRefresh = 60000; // 1 minuto
			if (msRefresh<10000) // 10 sec
				msRefresh = 10000;
			if (serviceRegisterInterval)
				clearInterval(serviceRegisterInterval);
			serviceRegisterInterval = setInterval(refreshEndpoint, msRefresh/2);
			refreshEndpoint();
		
			async function refreshEndpoint() {
				console.log('on refresh service '+serviceName+' endpoint');
				var exp = new Date().getTime()+msRefresh;
				var ret = await registerServiceEndpoint(serviceName, serviceUrl, exp, serviceAuthorization);
				console.log('refresh endpoint response: '+JSON.stringify(ret));
				return ret;
			}
		}
		
	
		return {
			getThisConfig:getThisConfig,
			getThisInfo: getThisInfo,
			setMyInfo: setMyInfo,
			getServiceInfo: getServiceInfo,
			startRegisterEndpointTimer: startRegisterEndpointTimer
		};
	}
	

	return {
		getServiceConfig:getServiceConfig,
		setServiceConfig:setServiceConfig,
		getServiceInfo: getServiceInfo,
		setServiceInfo: setServiceInfo,
		registerServiceEndpoint: registerServiceEndpoint,
		createService: createService
	};
}

module.exports = { create: create};