console.log('this is main.....');

// serve a fare in modo che nelle chiamate https siano accettati anche certificati autofirmati
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

//process.env.DEBUG = 'oidc-provider:*'; // dovrebbe loggare oidc
process.env.DEBUG = '*'; // dovrebbe loggare tutto
require('./eng-code/provider');

 