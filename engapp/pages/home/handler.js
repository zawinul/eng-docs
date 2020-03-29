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
			$('.login', div).click(doLogin);
			$('.login2', div).click(doLogin2);
		});
	}
	
	async function doLogin() {
		await engapp.load('oidc/client-utils.js');
		var data = await oidc.getCredentials(true); 
		$('.curr-user').text(data.user.personali.nome).show();
		

		console.log(data);
	}
	

		
	async function doLogin2() {
		await engapp.load('oidc/client-utils.js');
		var data = await oidc.getCredentials(false); 
		$('.curr-user').text(data.user.personali.nome).show();
		

		console.log(data);
	}

	engapp.navigation.registerPage({
		name: 'home',
		activate:activate,
		deactivate:deactivate,
		init:init
	});


})();