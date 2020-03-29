(function(){
	throw "engapp.restService non esiste più!";
	
	var div, base;
	var serv = engapp.restService([
		'http://country.register.gov.uk'
		//'http://127.0.0.1:4444'
	]);
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
		return engapp.load(div, base+"/content.html").then(onPageLoad);
	}
	
	function onPageLoad() {
		var table = $('.page-3-table', div);
		var tbody = $('tbody', table).empty();

		serv.get('records.json', {"page-size":5000}).then(onTableData);

		async function onTableData(data) {
			var i=1;
			var arr = [];
			for(var id in data) {
				var tr = $('<tr/>').appendTo(tbody);
				$('<td/>').text(i).appendTo(tr);
				$('<td/>').text(id).appendTo(tr);
				$('<td/>').addClass('name').html('&nbsp;').appendTo(tr);
				$('<td/>').addClass('ufficial-name').html('&nbsp;').appendTo(tr);
				arr.push({tr:tr, id:id});
				i++;
			}

			for(var i=0; i<arr.length; i++) {
				var {id, tr} = arr[i];
				let z = await serv.get('records/'+id+'.json');
				tr.children().eq(2).text(z[id].item[0].name);
				tr.children().eq(3).text(z[id].item[0]['official-name']);
			}

		}

	}

	engapp.navigation.registerPage({
		name: 'pagina-tre',
		activate:activate,
		deactivate:deactivate,
		init:init
	});

})()