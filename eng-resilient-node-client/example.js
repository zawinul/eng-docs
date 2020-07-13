const clientFactory = require('./eng-resilient-node-client');

// serve a fare in modo che nelle chiamate https siano accettati anche certificati autofirmati
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

async function main() {
	var client = clientFactory.create();
	var configOut = { conf_a:33, conf_b:44 };
	var infoOut = { info_a:55, info_b:66 };

	console.log('set service config');
	await client.setServiceConfig('pippo', configOut,'asdfghjk');

	console.log('set service info');
	await client.setServiceInfo('pippo', infoOut,'asdfghjk');
	
	console.log('get service config');
	var configIn = await client.getServiceConfig('pippo');
	console.log(configIn);

	console.log('get service info');
	var infoIn = await client.getServiceInfo('pippo');
	console.log(infoIn);

	console.log('ok');
}

main();
