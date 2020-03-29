const uuidv1 = require('uuid/v1');
const crypto = require('crypto');
const USERSdata = require('../eng-config/utenti.json');
const MYHASHKEY='UYSfGDfCeUhGV6Yy7U3YFvUYGUD6YG3DUdYDGUYGU';
function hash(x) {
	var h = crypto.createHmac('sha512', MYHASHKEY);
    h.update(x)
    return h.digest('hex');
}


var idMap = {}, loginMap={};

class Account {
	static inizializza() {
		for(var i=0; i<USERSdata.length; i++) {
			let x = USERSdata[i];
			let y = new Account(x.accountId);
			y.claimsData = x.claims;
			y.passwordHash = x.passwordHash || hash(x.password);
			idMap[x.accountId] = y;
			loginMap[x.login] = y;
		}
	}

	constructor(x) {
		this.accountId = x || uuidv1(); // the property named accountId is important to oidc-provider
		this.claimsData = {};
	}

	// claims() should return or resolve with an object with claims that are mapped 1:1 to
	// what your OP supports, oidc-provider will cherry-pick the requested ones automatically
	claims() {
		return Object.assign({}, this.claimsData, {
			sub: this.accountId
			// , altro_ancora: {
			// 	p:123,
			// 	q:new Date()
			// }
		});
	}

	static async findById(ctx, id) {
		return idMap[id];
	}


	
	static async findByLogin(login) {
		return loginMap[login];
	}

	async authenticate(password) {
		return hash(password)==this.passwordHash;
	}
};

Account.inizializza();

module.exports = Account;
