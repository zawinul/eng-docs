//const Provider = require('oidc-provider');
const Provider = require('oidc-provider');
const fs = require('fs');
const express = require('express');
const http = require('http');
const https = require('https');
const myDebug = require('./debug-provider');
const Account = require('./account');
const helmet = require('helmet');
const { urlencoded } = require('express'); // eslint-disable-line import/no-unresolved
const body = urlencoded({ extended: false });
const path = require('path');
const ejs = require('ejs');
const base64 = require('base-64');
const debug = require('debug');
const log = debug('provider');

const viewsDir = path.join(__dirname, '../eng-views')
const certDir = path.join(__dirname, '../certificati');
const configDir = path.join(__dirname, '../eng-config');
const keystore = require(path.join(certDir, '/keystore-private.json'));
const clientConfig = require(path.join(configDir, 'clients.json'));
//const storageAdapter = require('./mongodb_adapter');
const storageAdapter = require('./eng-storage-adapter.js');
const url = require('url');
const findFreePort = require("find-free-port")
const getConfig = require('./get-config.js');

const clientTemplate = {
	grant_types:  [
		"implicit",
		"authorization_code",
		"refresh_token"
	],
	response_types: [
		"code id_token token",
		"code id_token",
		"code token",
		"code",
		"id_token token",
		"id_token",
		"none"
	],
	tokenEndpointAuthMethods: [ 
		"none",
		"client_secret_basic",
		"client_secret_jwt",
		"client_secret_post",
		"private_key_jwt" 
	]
};

//log.log = console.log.bind(console); // don't forget to bind to console!
debug.log = console.log.bind(console);

var app;
var oidc;

async function main() {
	var configuration = await initConfiguration(); 
	app = startWebServer(configuration);
	oidc = await initOidc(configuration);
}

async function initConfiguration() {
	var conf = await getConfig.getConfig();

	async function logoutSource(ctx, form) {
		var p = path.join(viewsDir, 'logout.ejs');
		var s = fs.readFileSync(p, {encoding:'UTF-8'});
		var html = ejs.render(s, {form: form});
		ctx.body = html;
	}
	
	async function postLogoutRedirectUri(ctx) {
		var callerParams = ctx.req.query || {};
		var x = base64.encode(JSON.stringify(callerParams));
		return '/post-logout?q='+x;
	}

	async function audiences(ctx, sub, token, use) {
		// @param ctx   - koa request context
		// @param sub   - account identifier (subject)
		// @param token - the token to which these additional audiences will be passed to
		// @param use   - can be one of "access_token" or "client_credentials"
		//   depending on where the specific audience is intended to be allowed
		
		
		return undefined;
		//return ["eng-doc-domain"];
	}

	conf.findById = Account.findById;
	conf.logoutSource = logoutSource;
	conf.postLogoutRedirectUri = postLogoutRedirectUri;
	
	//conf.audiences = audiences;
	
	conf.interactionCheck = async function(ctx) { return false; }
	
	var packedUrl = new URL(conf.providerUrl);
	var requiredPort = packedUrl.port-0;
	var actualPort = await findFreePort(requiredPort);
	conf.port = actualPort[0];	
	packedUrl.port = conf.port;
	conf.providerUrl = packedUrl.href;
	getConfig.registerEndpoint(conf.providerUrl, 10000);

	return conf;
}


function startWebServer(configuration) {
	app = express();
	app.use(helmet());

	app.set('views', viewsDir);
	app.set('view engine', 'ejs');
	
	//http.createServer(app).listen(3000);
	const sslOptions = {
		key: fs.readFileSync(path.join(certDir, 'eng-key-encrypted.pem')),
		cert: fs.readFileSync(path.join(certDir, 'eng-cert.pem')),
		passphrase: 'makkina19'
	};

	routing();

	https.createServer(sslOptions, app).listen(configuration.port);
	return app;
}

async function initOidc(configuration) {
	var oidc = new Provider(configuration.providerUrl, configuration);
	myDebug(oidc);
	
	var clients = clientConfig.map(client => Object.assign({}, client, clientTemplate));
	console.log(JSON.stringify(clients, null,2));
	oidc.initialize({
		clients: clients,
		keystore: keystore,
		adapter: storageAdapter
	})
	.then(() => {
		app.use('/', oidc.callback);
		log('oidc-provider listening on port '+configuration.port);
		log('check '+configuration.providerUrl+'/.well-known/openid-configuration');
	})
	.catch((err) => {
		log(err);
		log(err.stack);
		process.exitCode = 1;
	});
	return oidc;	
}


function routing() {

	app.get('/post-logout', async (req, res, next) => {
		return res.render('post-logout');
	});
	
	app.get('/interaction/:grant', async (req, res, next) => {
		// il cliente chiede login+autorizzazione,
		// sto per saltare alla pagina di login-e-autorizzazione (se non sono loggato) 
		// o di autorizzazione
		try {
			const details = await oidc.interactionDetails(req);
			const client = await oidc.Client.find(details.params.client_id);
			log(details.interaction.error);
			var scopes = details.params.scope.split(' ');
			var pageData = {
				client,
				details,
				scopes,
				params: details.params,
				interaction: details.interaction
			};
			if (details.interaction.error === 'login_required') 
				return res.render('login-e-autorizzazione', pageData);
			else  
				return res.render('autorizzazione', pageData);
		} 
		catch (err) {
			return next(err);
		}
	});
	
	app.post('/interaction/:grant/confirm', body, async (req, res, next) => {
		// sono di ritorno da una pagina di autorizzazione
		try {
			// var codes = [];
			// for (var k in req.body) {
			// 	if (k.indexOf('scope_')==0 && req.body[k]=='yes') 
			// 		codes.push(k.substring(6));
			// }
			const result = { 
				// consent:  {
				//  	scope: "pippo pluto paperino"
				// } 
			};
			const details = await oidc.interactionDetails(req);
			await oidc.interactionFinished(req, res, result);
		} catch (err) {
			next(err);
		}
	});
	
	app.post('/interaction/:grant/login', body, async (req, res, next) => {
		// sono di ritorno da una pagina di login-e-autorizzazione
		var details = await oidc.interactionDetails(req);
		try {
			var {login, password} = req.body;
			const account = await Account.findByLogin(login);
			// var codes = [];
			// for (var k in req.body) {
			// 	if (k.indexOf('scope_')==0 && req.body[k]=='yes') 
			// 		codes.push(k.substring(6));
			// }
			var result;
			if (!account) {
				result = {
					error: 'access_denied',
					error_description: 'Account inesistente o password sbagliata'
				};
			}
			else {
				const authenticated = await account.authenticate(password);
				if (authenticated) {
					result = {
						login: {
							account: account.accountId,
							acr: 'urn:mace:incommon:iap:bronze',
							amr: ['pwd'],
							remember: !!req.body.remember,
							ts: Math.floor(Date.now() / 1000),
							custom1 : 'c1' 
						},
						meta: {
							custom2 : 'c2'
						}

						// ,consent: {
						//  	scope: "pippo pluto paperino"
						// }
					};
				}
				else {
					result = {
						error: 'access_denied',
						error_description: 'Account inesistente o password sbagliata'
					};
				}
			};
	
			await oidc.interactionFinished(req, res, result);
		} catch (err) {
			next(err);
		}
	});
	


	// attenzione: solo per debug
//	const instance = require('../oidc-provider/lib/helpers/weak_cache.js');
	const JWT = require('oidc-provider/lib/helpers/jwt');

	app.post('/genera-token', body, async (req, res, next) => {

		try {
			var payload = JSON.parse(req.body.payload) || {};
			var ks = instance(oidc).keystore;
			const key = ks.get({alg: 'RS256', use: "sig"} );
			var y = await JWT.sign(payload, key, 'RS256', {});

			res.send(y);
		}
		catch(e) {
			console.log(e);
		}
	});
}

main();
