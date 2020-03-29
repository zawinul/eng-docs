
const engMsService = require('./engMsService.js');
var service;

function main() {
	var cfg = {
		name: 'MSENG_EXAMPLE',
		secret: 'mysecret', 
		url: 'http://localhost:5555/',
		logfile:'myservice.log'
	};

	engMsService.init(cfg).then(function(srv) {
		service = srv;
		// service.preValidation = function(req) {
		// 	console.log('prevalidation '+req.method+' '+req.path);
		// 	req.isValidated = true;
		// }

		service.app.get('/pippo', function (req, res) {
			res.status(200).send('ciao pippo');
		});
	
		service.app.get('/me', function (req, res) {
			res.status(200).send(
				JSON.stringify(req.userInfo, null, 2)
			);
		});
	
		service.startServer();
		service.registerToDiscovery();
		service.logger.info('STARTED!!!');
	
	}).catch(function(a) {
		console.log(a);
		throw a;
	});

}

main();