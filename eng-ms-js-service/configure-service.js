//const configDefaultUrl = "http://eng-config:5678/api/v1";
const randomstring = require("randomstring");

const registryClientFactory = require('eng-resilient-node-client');
//var registeredServiceFactory = require('./eng-registered-service');

async function configure(instanceDefinition) {

	var defaultConfig = {
		name: ('SRV'+Math.random()).substring(0,10),
		url: 'https://localhost:8080',
		secret: 'no secret'
	};


	var conf = Object.assign({}, defaultConfig, instanceDefinition);

	if (!scanArgs(conf)) {
		syntax();
		return null;
	}
	conf.instanceName = conf.instanceName || randomstring.generate(7).toUpperCase();
	conf.logfile = conf.logfile || conf.instanceName+'.log';

	var server = { config: conf }
	//configClient = conf.configClient = configClientFactory.build(conf.configUrl, conf.name, conf.secret);
	if (!conf.noRegistry) {
		var c = await registryClientFactory.create(conf.registryEndpoints);
		server.registryClient = c;

		var s = c.createService(conf.name, conf.authorization);
		server.registeredService = s;
		
		var externalConfig = await s.getThisConfig();
		Object.assign(conf, externalConfig);
	}
	return server;
}

var configurable = ['httpURL', 'httpsURL', 'instanceName', 'registryEndpoint'];

function scanArgs(conf){
	console.log('working dir: '+process.cwd());
	
	for(var i=0; i<process.argv.length; i++) 
		console.log(`argv[${i}] = ${process.argv[i]}`);
	
	var args = process.argv.slice(2);
	var i=0; 
	while(true) {
		if (i>=args.length)
			break;
		let ok = false;
		for(var parName of configurable) {
			if (args[i]=='-'+parName) {
				i++;
				if (i>=args.length)
					return false;

				if (Array.isArray(conf[parName]))
					conf[parName].push(args[i]);
				else
					conf[parName] = args[i];
				
				ok = true;	
			}
		}
		if (!ok) 
			return false;
		i++;
	}
	return true;
}

function syntax() {
	console.log(
		"ERROR in command, syntax: node main.js ( -<optionName> <optionValue> )*\n"
		+ "options\n"
		+ configurable.map(x=>'    '+x).join('\n')
	);
}

module.exports = {configure: configure};