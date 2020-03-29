const express = require('express');
const http = require('http');
const https = require('https');
const fs = require('fs');
const parseOidcAuthentication = require('./parse-oidc-authentication');
const winston = require('winston');
const path = require('path');
const cors = require('cors');
const YAML = require('yamljs');
const findFreePort = require("find-free-port");
const morgan = require('morgan');
const configureService = require('./configure-service');

// serve a fare in modo che nelle chiamate https siano accettati anche certificati autofirmati
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";


async function init(instanceDefinition) {

	var service = await configureService.configure(instanceDefinition);

	service.logger = initLogger(service.config);
	service.logger.info(`starting ${service.config.name}`);
	await initExpress(service);
	service.addSwagger = addSwagger;

	service.addStaticDir = function(staticDir) {
		service.rootApp.use(express.static(staticDir));
	};
	
	logger.info(`service ${service.config.name} started!`);

	service.getCredentials = async function(req) {
		try {
			var ok = await parseOidcAuthentication(req);
			return ok ? req.userInfo : null;
		}
		catch(e) {
			console.log(e);
			return null;
		}
	}

	
	async function defaultAuthorizationLogic(req, service) {
		// permetti tutto ai soli utenti loggati
		var x = await service.getCredentials();
		return !!x;
	}
	service.authorizeRequest = defaultAuthorizationLogic;

	service.registerToDiscovery = async function() {
		var service = this;
		var ms = service.config.msRegistryRefresh || 10000;
		if (service.config.httpURL)
			service.registeredService.startRegisterEndpointTimer(service.config.httpURL, ms);
		if (service.config.httpsURL)
			service.registeredService.startRegisterEndpointTimer(service.config.httpsURL, ms);
	}
	return service;
}

function initLogger(config) {

	const myFormat = winston.format.printf(({ level, message, label, timestamp }) => {
		return `${timestamp} [${label}] ${level}: ${message}`;
	});

	const consoleFormat = winston.format.combine(
		winston.format.label({ label: config.name }),
		winston.format.timestamp(),
		myFormat
  	);

	logger = winston.createLogger({
		level: 'debug',
		format: winston.format.json(),
		defaultMeta: {
			service: config.name
		},
		transports: [
			new winston.transports.Console({
				level: 'debug', 
				format: consoleFormat 
			}),
			new winston.transports.File({ 
				filename: config.logfile 
			})
		]
	});
	
	logger.stream = {
		write: function(message, encoding) {
		  // use the 'info' log level so the output will be picked up by both transports (file and console)
		  logger.info(message);
		}
	  };
	  
	logger.info('logger started');
	return logger;
}

function addSwagger(yamlFilePath) {
	try {
		var server = this;
		const swaggerUi = require('swagger-ui-express');
		var yamlString = fs.readFileSync(yamlFilePath, {encoding:'utf8'});
		var swaggerDocument = YAML.parse(yamlString);
		swaggerDocument.servers = [];
		if (server.config.httpURL)
			swaggerDocument.servers.push({url:server.config.httpURL, description:'non sicuro'});
		if (server.config.httpsURL)
			swaggerDocument.servers.push({url:server.config.httpsURL, description:'sicuro'});
		server.rootApp.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
	} catch(e) {
		console.log(e);
	}
}

async function initExpress(service) {
	var config = service.config;
	var logger = service.logger;

	if (config.httpURL) {
		let packedUrl = new URL(config.httpURL);
		let requiredPort = packedUrl.port-0;
		logger.info('required http port ' + requiredPort);
		config.httpPort = (await findFreePort(requiredPort))[0];
		logger.info('obtained http port ' + config.httpPort);
		packedUrl.port = config.httpPort;
		config.httpURL = packedUrl.href;	
		if (config.httpURL.endsWith('/'))
			config.httpURL = config.httpURL.substring(0, config.httpURL.length-1);

		logger.info('starting http on port '+config.httpPort);
		logger.info('URL: '+config.httpURL);	
	}

	if (config.httpsURL) {
		let packedUrl = new URL(config.httpsURL);
		let requiredPort = packedUrl.port-0;
		logger.info('required https port ' + requiredPort);
		config.httpsPort = (await findFreePort(requiredPort))[0];
		logger.info('obtained https port ' + config.httpsPort);
		packedUrl.port = config.httpsPort;
		config.httpsURL = packedUrl.href;	
		if (config.httpsURL.endsWith('/'))
			config.httpsURL = config.httpsURL.substring(0, config.httpsURL.length-1);
		logger.info('starting https on port '+config.httpsPort);
		logger.info('URL: '+config.httpsURL);
	}

	
	var rootApp = service.rootApp = express();
	var apiApp = service.apiApp = rootApp;
	if (config.apiPrefix) {
		apiApp = service.apiApp = express.Router();
		rootApp.use(config.apiPrefix, apiApp);
	}


	rootApp.use(function(req, res, next){
		console.log(req.method+' '+req.path);
		next();
	});
	rootApp.use(morgan('combined', { stream: logger.stream }));
	apiApp.use(cors());
	apiApp.use(express.json());       // supporta JSON-encoded bodies
	apiApp.use(express.urlencoded()); // supporta URL-encoded bodies

	apiApp.use('/alive', function (req, res, next) {
		// anche senza autenticazione
		res.status(200).send({msg:'OK'});
	});



	// authorization
	apiApp.use(async function(req, res, next) {
		var ok = await service.authorizeRequest(req, service);
		if (ok)
			next();
		else
			res.status(400).send("Unauthorized!");
	});

	service.startServer = function() {
		var server = this;
		if (config.httpsURL) {
			const certificateDirectory = path.join(__dirname, '/certificati');
			const certificateFile = config.certificateFile || path.join(certificateDirectory, 'my-https-cert.pem');
			const encriptedKeyFile = config.encriptedKeyFile || path.join(certificateDirectory, 'my-https-key-encrypted.pem');
			const encriptedKeyPassPhrase = config.encriptedKeyPassPhrase || 'ZHCC JTXP';
	
			const sslOptions = config.sslOptions ||  {
				cert: fs.readFileSync(certificateFile),
				key: fs.readFileSync(encriptedKeyFile),
				passphrase: encriptedKeyPassPhrase,
				port: config.httpsPort
			};
	
			var httpsServer = https.createServer(sslOptions, rootApp);
			httpsServer.listen(config.httpsPort);
			logger.info('HTTPS server started at port '+config.httpsURL);
			console.log('HTTPS server started at port '+config.httpsURL);
		}

		if (config.httpURL) {
			var httpServer = http.createServer(rootApp);
			httpServer.listen(config.httpPort);
			logger.info('HTTP server started at port '+config.httpURL);
			console.log('HTTP server started at port '+config.httpURL);
		}
	};
}


module.exports = {
	init: init
};


