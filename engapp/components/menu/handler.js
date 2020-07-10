(function(){



function create() {
	var ret = $.Deferred();
	var div = $('<div/>');
	
	engapp.load("components/menu/menu.css");
	engapp.load(div, "components/menu/menu.html").then(function(){

		var content = div.children();
		$('a', content).not('[href]').attr('href', 'javascript:void(0)');

		$('[open-page]', content).click(function(evt) {
			evt.preventDefault();
			$('#navbar').collapse("hide")
			var dest = $(this).attr('open-page');
			engapp.navigation.openPage(dest);
		});

		if (oidc.isLogged()) {
			oidc.getCredentials().then(function(data) {
				var nome = data.user.personali.nome;
				$('.curr-user', div).text(nome).removeClass('hidden').addClass('ciao');
			});
		}

		var returnedObj = {
			content: content,
			
			setMenuActive: function(label) {
				$('.active', content).removeClass('active');
				$('.'+label, content).addClass('active');
			}
		};

		ret.resolve(returnedObj);
	});
	return ret;
}


engapp.components.menu = {
	create: create
};


	
})();


 