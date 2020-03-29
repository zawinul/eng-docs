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

		return engapp.load(div, base+"/content.html")
		.then(()=>engapp.load('js/camunda.js'))
		.then(()=>{
			$('.ok', container).show();
			$('.procedi', container).click(procedi);	
		});
	}

	function procedi() {
		var fields = {
			mittente: $('.mittente').val(),
			destinatario: $('.destinatario').val(),
			oggetto: $('.oggetto').val(),
			testo: $('.testo').val(),
		};

		camunda.startProcessByName('invio_R3', 'BK'+Math.random(), fields).then(function(x) {
			$('.dump').text(JSON.stringify(x, null,2));
		});
	}

	engapp.navigation.registerPage({
		name: 'r3/richiedi',
		activate:activate,
		deactivate:deactivate,
		init:init
	});

})();