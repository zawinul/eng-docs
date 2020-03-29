//const Provider = require('oidc-provider');
const Provider = require('oidc-provider');
var conf = require('../config/provider-configuration.json');

const path = require('path');
const base64 = require('base-64');
const keystore = require('../certificati/keystore-private.json');

const {
	JWK: { isKeyStore },
	JWS: { createSign, createVerify },
	JWE: { createEncrypt, createDecrypt },
} = require('node-jose');

const { stringify, parse } = JSON;

const format = 'compact';
const typ = 'JWT';
	
var oidc = 	new Provider('https://oidc-provider:3043', conf);

oidc.initialize({
	keystore: keystore
})
.then(doit)
.catch((err) => {
	console.log(err);
	process.exitCode = 1;
});

function doit() {
	var payload  = {
		"jti": "lsL4MlmlmENoRQWBPctCa",
		"sub": "23121d3c-84df-44ac-b458-3d63a9a05497",
		"iss": "https://oidc-provider:3043",
		"iat": 1536068978,
		"exp": 1536072578,
		"scope": "openid profile dati_applicativi altro servizio1 servizio5",
		"aud": "foo"
	};
	
	var alg = "RS256";
	const key = oidc.keystore.get( alg, "sign" );
	var fields = {
		alg:"RS256", 
		typ:"JWT"
	};
	
	var x = createSign({fields, format}, { key, reference: key.kty !== 'oct' })
		.update(JSON.stringify(payload), 'utf8')
		.final();
	
	console.log(x);
	
}
