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

function EngStorageAdapter(model) {
	function key(id) {
		return `${this.model}:${id}`;
	};

	async function destroy(id) {
		const key = this.key(id);
		storage.del(key);
	}

	async function consume(id) {
		storage.get(this.key(id)).consumed =  Math.floor(new Date().getTime() / 1000);
	}

	async function find(id) {
		return storage.get(this.key(id));
	}

	async function findByUid(uid) {
		const id = storage.get(sessionUidKeyFor(uid));
		return this.find(id);
	}

	async function findByUserCode(userCode) {
		const id = storage.get(userCodeKeyFor(userCode));
		return this.find(id);
	}

	async function upsert(id, payload, expiresIn) {
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

	async function revokeByGrantId(grantId) { // eslint-disable-line class-methods-use-this
		const grantKey = grantKeyFor(grantId);
		const grant = storage.get(grantKey);
		if (grant) {
			grant.forEach((token) => storage.del(token));
			storage.del(grantKey);
		}
	}

	return {
		model: model,
		destroy: destroy,
		consume: consume,
		find: find,
		findByUid:findByUid,
		findByUserCode: findByUserCode,
		upsert: upsert,
		revokeByGrantId: revokeByGrantId,

		key: key
	};
} 

module.exports = EngStorageAdapter;
module.exports.setStorage = (store) => { storage = store; };
