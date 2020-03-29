

(function(){



engapp.utils = {
	getConfig: function(){
		return Promise.resolve({});
	},

	getUrlParams: function(url) {
		var ret = {};
		if (url.indexOf('?') < 0)
			return ret;
	
		url.split('?')[1].split('&').map(function (x) {
			var y = x.split('=');
			ret[y[0]] = unescape(y[1]);
		});
		return ret;
	}

	//....
	//....
}




})();
