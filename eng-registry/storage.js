const fs = require("fs");
const DATAFILEPATH = "c:/workspace/eng-docs/data";


function readJsonFile(name, defaultData) {
	const filePath = DATAFILEPATH+'/'+name+'.json';
	return new Promise(function(resolve, reject) {
		fs.readFile(filePath,  {encoding:'utf8'}, function(err, data){
			try {
				if (err) 
					resolve(defaultData);
				else
					resolve(JSON.parse(data));
			}catch(e) {
				console.log(e);
				return defaultData;
			}
		});
	});
}

function writeJsonFile(name, value) {
	const filePath = DATAFILEPATH+'/'+name+'.json';
	return new Promise(function(resolve, reject) {
		fs.writeFile(filePath, JSON.stringify(value,null,2),  {encoding:'utf8'}, function(err){
			if (err)
				reject(err);
			else
				resolve(value);
		});
	});
}

function init() {
}


function getInfo(key){
	return readJsonFile('info', {}).then(data=>data[key]);
}

function setInfo(key, value) {
	return readJsonFile('info', {}).then(info=> {
		info[key] = value;
		return writeJsonFile('info', info).then(()=>info[key]);
	});
}

function getConfig(key) {
	return readJsonFile('config', {}).then(config=>config[key]);
}

function setConfig(key, value) {
	return readJsonFile('config', {}).then(config=> {
		config[key] = value;
		writeJsonFile('config', config);
		return config[key];
	});
}

async function deleteExpiredEndpoints(endpoints, writeOnChange)  {
	var changed = false;
	for(var i=endpoints.length-1; i>=0; i--) {
		var ep = endpoints[i];
		if (ep.expire<=new Date().getTime()) {
			console.log('expired '+ep.service+' '+ep.url);
			endpoints.splice(i, 1);
			changed = true;
		}
	}
	if (changed && writeOnChange)
		return writeJsonFile('endpoint', endpoints).then(()=>endpoints);
	else
		return endpoints;
}

async function getEndpoint(service) {
	var endpoints = await readJsonFile('endpoint', []);
	endpoints = await deleteExpiredEndpoints(endpoints, true);
	var ret = endpoints.filter(ep=>ep.service==service).map(ep=>ep.url);
	return ret;
}

async function setEndpoint(service, url, expire) {
	var endpoints = await readJsonFile('endpoint', []);
	endpoints = await deleteExpiredEndpoints(endpoints, false);
	var found = null;
	for(var ep of endpoints) {
		if (ep.url==url && ep.service==service) {
			ep.expire =expire;
			found = ep;
		}
	}
	if (!found) {
		found = {service:service, url:url, expire: expire};
		endpoints.push(found);
	}
	await writeJsonFile('endpoint', endpoints);
	return found;
}

async function getAll() {
	var info = await readJsonFile('info');
	var config = await readJsonFile('config');
	var endpoint = await readJsonFile('endpoint');
	return {
		info: info,
		config: config,
		endpoint: endpoint
	};
}

init();

module.exports = {
	getInfo: getInfo,
	setInfo: setInfo,
	getConfig: getConfig,
	setConfig: setConfig,
	getEndpoint: getEndpoint,
	setEndpoint: setEndpoint,
	getAll: getAll
};