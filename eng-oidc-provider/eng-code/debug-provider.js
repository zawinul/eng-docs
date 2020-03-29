var errorEvents = [
	'authorization.error',
	'grant.error',
	'discovery.error',
	'introspection.error',
	'revocation.error',
	'registration_create.error',
	'registration_read.error',
	'check_session_origin.error',
	'userinfo.error',
	'check_session.error',
	'webfinger.error',
	'server_error',
	'backchannel.error'
];

var otherEvents = [
	'authorization.accepted',
	'interaction.started',
	'interaction.ended',
	'authorization.success',
	'grant.success',
	'certificates.error',
	'registration_create.success',
	'registration_update.success',
	'registration_update.error',
	'registration_delete.success',
	'registration_delete.error',
	'end_session.success',
	'end_session.error',
	'token.issued',
	'token.consumed',
	'token.revoked',
	'grant.revoked',
	'backchannel.success',
];


function attachErrorEvents(provider) {
	function evnt(evName) {
		provider.on(evName, function(){
			console.log('ERROR: \t!!\t!!\t', evName);
			for(var i=0; i<arguments.length; i++) {
				console.log('ERROR: \t  \t  \t'+arguments[i]);
			}
		});
	}
	
	errorEvents.map(evnt);
}

function attachOtherEvents(provider) {
	function evnt(evName) {
		provider.on(evName, function(){
			console.log('\t>>\t>>\t', evName);
		});
	}
	
	otherEvents.map(evnt);
}

function doDebug(provider) {
	provider.use(async (ctx, next) => {
		console.log(' ** middleware pre', ctx.method, ctx.path);	// ...
		await next();
		console.log(' ## middleware post', ctx.method); // ...
	});
	
	attachErrorEvents(provider);
	attachOtherEvents(provider);
}

module.exports = doDebug;