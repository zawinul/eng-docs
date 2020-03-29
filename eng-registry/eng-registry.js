const fs = require('fs');
const engMsService = require('../eng-ms-js-service');
const operations = require('./operations.js');

async function main() {
	var initialConfig = {
		name: 'eng-registry',
		instanceName: 'eng-reg-default',
		httpURL: null,
		httpsURL: 'https://eng-registry:5432',
		noRegistry: true,
		secret: 'y8.+4yr8b7wd87y',
		apiPrefix: '/api/v1'
	};

	const service = await engMsService.init(initialConfig);


		
	service.addSwagger('./eng-registry.yaml');
	service.addStaticDir('./static');
	service.authorizeRequest = authorize;

	operations.init(service.apiApp);

	service.startServer();
	// service.registerToDiscovery();
	return 'STARTED!!!';

}

// logica di autorizzazione:
async function authorize(req, service) {
	if (req.path=='/test')
		return true;
	if (req.path.startsWith('/service/endpoint/'))
		return true;
	if (req.socket.remoteAddress.endsWith('127.0.0.1'))
		return true;
	if (req.header.debugCredentials=='kf93nc0e09d')
		return true;

	var credentials = await service.getCredentials(req);
	return !!credentials;
}

main().then(console.log);
