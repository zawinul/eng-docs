const engMsService = require('../eng-ms-js-service');

// opzioni iniziali
var cfg = {
	name: 'servizio1',
	httpsURL: 'https:/localhost:6543',
	logfile:'servizio1.log'
};

// logica di autorizzazione:
function authorize(req, service) {
	if (req.path=='/pippo')
		return true;
	if (req.path=='/test')
		return true;

	return service.getCredentials(req)
	.then(function(cred) {
		return cred!=null;
	});
}

function initPaths(service) {
	
	var pippoCount = 0;
	service.apiApp.get('/pippo', function (req, res) {
		var saluto = req.userInfo ? req.userInfo.messaggio : 'ciao forestiero';
		var message = `${saluto}, io sono ${service.config.instanceName}, questa è la richiesta #${pippoCount}`;
		pippoCount++;
		res.status(200).send(message);
	});

	var testCount=0;
	service.apiApp.get('/test', function (req, res) {
		var result = {
			count: testCount++,
			instance: service.config.instanceName,
			userInfo: req.userInfo 
		};
		res.status(200).send(JSON.stringify(result));
	});

	service.apiApp.get('/pluto', function (req, res) {
		var saluto = req.userInfo ? req.userInfo.messaggio : 'ciao forestiero';
		var message = `${saluto}, io sono ${service.config.instanceName}, questa è la richiesta #${pippoCount}`;
		pippoCount++;
		res.status(200).send(message);
	});

	service.apiApp.get('/me', function (req, res) {
		res.status(200).send(
			JSON.stringify(req.userInfo, null, 2)
		);
	});
}

async function main() {
	var service = await engMsService.init(cfg);
	service.authorizeRequest = authorize;
	initPaths(service);
	service.startServer();
	service.registerToDiscovery();
	service.logger.info('STARTED!!!');
}
main();



