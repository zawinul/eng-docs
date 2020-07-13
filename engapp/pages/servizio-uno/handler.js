(function(){
	var div, base;
	var engappConfigClient;
	var servizio1Client;
	var timer;
	var tabella;

	var results = {};

	function activate(container, params) {
		div.appendTo(container);
		timer = setInterval(callService, 1000);
	}
		
	
	function deactivate(container, params) {
		clearInterval(timer);
		div.detach();
	}
	
	function init(container, baseUrl) {
		base = baseUrl;
		div = $('<div/>').appendTo(container);
		engapp.load(base+"/style.css");
		initialized = initClient();
		
		return engapp.load(div, base+"/content.html")
		.then(initClient)
		.then(function(){ 
			return engapp.creaComponente('tabella');
		})
		.then(function(_tabella) {
			tabella = _tabella;
			tabella.setData(['a','b','c'],[['d','e','f'],['d','e','f'],['d','e','f'],['d','e','f']]);
			$('.table-space', div).append(tabella.content);
		});
	}

	async function callService() {
		var x = await servizio1Client.get('/test');
		var y = x.instance;
		if (!results[y])
			results[y] = 1;
		else
			results[y]++;
		
		var instances = Object.keys(results).sort();
		var data = instances.map(inst=>[inst, ""+results[inst]]);
		tabella.setData(['instanza', 'count'], data);

		console.log(x);


	}

	async function initClient() {
		var app = await getThisResilientApp();
		servizio1Client = await app.getServiceClient('servizio1');
	}

	async function testconfig() {

		var cc = await getConfigClient('engapp');
		console.log({cc:cc});
		var s1 = await cc.getServiceClient('servizio1');
		console.log({s1:s1});
		var pippo = await s1.get('/pippo');
		console.log({pippo:pippo});
	}

	engapp.navigation.registerPage({
		name: 'servizio-uno',
		activate:activate,
		deactivate:deactivate,
		init:init
	});

})()