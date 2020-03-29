(function(){
	var div, base;
	
	function activate(container, params) {
		div.appendTo(container);
	}
		
	
	function deactivate(container, params) {
		div.detach();
	}
	
	function init(container, baseUrl) {
		base = baseUrl;
		div = $('<div/>').appendTo(container);
		engapp.load(base+"/style.css");

		return engapp.load(div, base+"/content.html").then(function(){
			oidc.getCredentials().then(function(data){
				console.log(data);
				$('.access-token', div).text(data.access_token);
				$('.user-info', div).text(JSON.stringify(data.user,null,4));
			});
		});
	}
	
	engapp.navigation.registerPage({
		name: 'token',
		activate:activate,
		deactivate:deactivate,
		init:init
	});

})()