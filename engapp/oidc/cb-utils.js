function processCallbackPage() {
	startProcessCallbackPage();
}

async function startProcessCallbackPage() {

	console.log('processCallbackPage');
	var x = await parseOidcCallback();
	console.log('parseOidcCallback received');
	var y = { // x semolificato
		id_token: x.id_token,
		id: x.id,
		access_token: x.access_token,
		user: x.user,
		exp: x.user.exp
	};

	$('.dump').text(JSON.stringify(y, null,2));
	var params = x.state;

	if (params.dataToParent) { // ! same page
		var msg = {msg:"onOidcData",listenerId:params.listenerId, payload:y};
		window.opener.postMessage(JSON.stringify(msg,null,2), "*");
	}
	else if (params.restartFrom) { // same page
		if (verificaToken(x)) {
			localStorage.engSingleSignOnData = JSON.stringify(y, null,2);
			setTimeout(function() {
				location.href = params.restartFrom;
			}, 1);
		}
		else {
			location.href = 'authentication-failed.html?restart='+escape(btoa(params.restartFrom));
		}
	}
}

function verificaToken(x) {
	console.log({verificaToken: x});
	var b1 = x.state.mark^localStorage.hmark^localStorage.hsecret;
	var b2 = x.user.nonce^localStorage.hnonce^localStorage.hsecret;

	if (b1!=0 || b2!=0) {
		alert('non mi quadra!!!');
		return false;
	}
	else {
		delete localStorage.hmark;
		delete localStorage.hnonce;
		delete localStorage.hsecret;
		return true;
	}
}


async function parseOidcCallback() {
	console.log('parseOidcCallback');

	var obj = {};
	var resp = location.href.split('#')[1];
	resp.split('&').map(function (x) {
		var y = unescape(x).split('=');
		obj[y[0]] = y[1];
	});

	var state = JSON.parse(atob(obj.state));
	console.log('calling userInfoFromToken');

	//var user = await userInfoFromToken(obj.access_token);
	var user = JSON.parse(atob(obj.id_token.split('.')[1]));
	var result = {
		obj:obj,
		state:state,
		access_token: obj.access_token,
		id_token: obj.id_token,
		exp: user.exp,
		user:user
	};
	console.log({oidcReceived:result});

	if (state.msgToParent) {
		console.log('sending message to parent');
		var msg = {
			msg: "onOidcData", 
			listenerId:state.listenerId, 
			payload:result
		};
		window.top.postMessage(JSON.stringify(msg), "*");
	}
	return result;
}


async function userInfoFromToken(accessToken) {
	var get = $.ajax('https://oidc-provider:3043/me',{
		method:'GET',
		dataType: 'json', 
		headers: {
			Authorization: 'Bearer '+accessToken
		}
	});

	get.catch(function(error) {
		ret.reject(error);
		console.log("getUserInfo error", error);
	});
	return get;
}
