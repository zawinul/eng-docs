const LRU = require('lru-cache');

let storage = new LRU({});

function grantKeyFor(id) {
	return `grant:${id}`;
}

function sessionUidKeyFor(id) {
	return `sessionUid:${id}`;
}

function userCodeKeyFor(userCode) {
	return `userCode:${userCode}`;
}

class EngStorageAdapter {

	// REQUESTED
	constructor(model) {
		this.model = model;
	}

	key(id) {
		return `${this.model}:${id}`;
	}

	// REQUESTED
	async destroy(id) {
		const key = this.key(id);
		storage.del(key);
	}

	// REQUESTED
	async consume(id) {
		storage.get(this.key(id)).consumed =  Math.floor(new Date().getTime() / 1000);
	}

	// REQUESTED
	async find(id) {
		return storage.get(this.key(id));
	}

	// REQUESTED
	async findByUid(uid) {
		const id = storage.get(sessionUidKeyFor(uid));
		return this.find(id);
	}

	// REQUESTED
	async findByUserCode(userCode) {
		const id = storage.get(userCodeKeyFor(userCode));
		return this.find(id);
	}

	// REQUESTED
	async upsert(id, payload, expiresIn) {
		const key = this.key(id);

		if (this.model === 'Session') {
			storage.set(sessionUidKeyFor(payload.uid), id, expiresIn * 1000);
		}

		const { grantId, userCode } = payload;
		if (grantId) {
			const grantKey = grantKeyFor(grantId);
			const grant = storage.get(grantKey);
			if (!grant) {
				storage.set(grantKey, [key]);
			} else {
				grant.push(key);
			}
		}

		if (userCode) {
			storage.set(userCodeKeyFor(userCode), id, expiresIn * 1000);
		}

		storage.set(key, payload, expiresIn * 1000);
	}

	// REQUESTED
	async revokeByGrantId(grantId) { // eslint-disable-line class-methods-use-this
		const grantKey = grantKeyFor(grantId);
		const grant = storage.get(grantKey);
		if (grant) {
			grant.forEach((token) => storage.del(token));
			storage.del(grantKey);
		}
	}
}

module.exports = EngStorageAdapter;
module.exports.setStorage = (store) => { storage = store; };
