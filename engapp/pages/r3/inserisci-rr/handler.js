
(function(){

	var div, base;
	var params;
		
	function activate(container, _params) {
		console.log('activate inserisci-rr');
		params = _params;
		div.appendTo(container);
	}
		

	function deactivate(container, params) {
		console.log('deactivate inserisci-rr');
		div.detach();
	}

	function init(container, baseUrl) {
		console.log('init inserisci-rr base='+baseUrl);
		base = baseUrl;
		div = $('<div/>').appendTo(container);
		//engapp.load(base+"/style.css");

		return engapp.load(div, base+"/content.html")
		.then(()=>engapp.load('js/camunda.js'))
		.then(function(){
			$('.invia', div).click(invia);
		});
	}



	function invia() {
		var rr = $('#rr', div).val();
		console.log('register rr '+rr);
		camunda.sendMessageByCorrelation('ricevutaRitornoArrivata', {reconciliationId:rr}, {fonteRR:'web'})
		.then(function(obj){
			$('.dump', div).text(JSON.stringify(obj, null,2));
		})
	}

	engapp.navigation.registerPage({
		name: 'r3/inserisci-rr',
		activate:activate,
		deactivate:deactivate,
		init:init
	});
})();

