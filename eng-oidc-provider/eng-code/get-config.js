const axios = require('axios');
const Resilient = require('resilient');

const CONFIGSECRET = 'IHIHIHUHUIHIUHg33';
const myDefaultUrl = "https://oidc-provider:3043";
const registryClientFactory = require('../../eng-registry-node-client');
const defaultProviderConfiguration = require('../eng-config/provider-configuration.json');
var registryClient;
var registeredService;

//http://eng-config:5678/api/v1/service/config/oidc-provider

function scanArgs(conf){
	var args = process.argv.slice(2);
	var i=0; 
	while(true) {
		if (i>=args.length)
			break;
		if (args[i]=='-providerURL') {
			i++;
			if (i>=args.length)
				return false;
			conf.providerUrl = args[i];	
		}
		else if (args[i]=='-configURL') {
			i++;
			if (i>=args.length)
				return false;
			conf.configUrl = args[i];	
		}
		else 
			return false;
		i++;
	}
}

function syntax() {
	console.log("ERROR in command, syntax= node main.js [-providerURL <this url>] [-configURL <config-url>]");
}

async function getConfig() {


	var conf = {};
	//var conf = require('../eng-config/provider-configuration.json');
	conf.providerUrl = myDefaultUrl;
	if (scanArgs(conf)) {
		syntax();
		return null;
	}
	registryClient = await registryClientFactory.create(conf.registryEndpoints);

	registeredService = registryClient.createService('oidc-provider', CONFIGSECRET);
	//conf.registeredService = s;

	
	var externalConfig = await registeredService.getThisConfig();
	Object.assign(conf, defaultProviderConfiguration, externalConfig);

	return conf;
}

function registerEndpoint(url, msRefresh) {
	if (url.endsWith('/'))
		url = url.substring(0, url.length-1);
	registeredService.startRegisterEndpointTimer(url, msRefresh||60000);
}

module.exports = {
	getConfig: getConfig,
	registerEndpoint: registerEndpoint
};