(function(){


var initialized=false;
var templatePromise;


function init() {
	engapp.load("components/tabella/style.css");
	initialized = true;
	var div = $('<div/>');
	templatePromise = engapp.load(div, 'components/tabella/content.html').then(function() {
		return div.children();
	});
}

function fillCell(td, val, i, j) {
	if (typeof (val)=='string') 
		td.text(val);
	else if ((typeof(val)=='object') && val.jquery)
		td.append(val);
	else if (val)
		td.text(val.toString());
	else
		td.html("&nbsp;");
}

function _setData(intestazioni, dati, element, trTemplate) {

	var head = $('thead>tr', element);
	$('td', head).remove();
	for(var i=0; i<intestazioni.length; i++) {
		var td = $('<td/>').appendTo(head);
		fillCell(td, intestazioni[i], -1, i);
	}

	$('tbody tr', element).remove();
	for(var i=0; i<dati.length; i++) {
		let tr = trTemplate.clone();
		let row = dati[i];
		for(var j=0; j<row.length; j++) {
			var td = $('<td/>').appendTo(tr);
			fillCell(td, row[j], i, j);					
		}
		$('tbody', element).append(tr);
	}

	return this;
}

function create() {

	if (!initialized)
		init();

	return templatePromise.then(function(template) {
		var element = template.clone();
		var trTemplate = $('tbody>tr', element).detach();

		function setData(intestazioni, dati) {
			_setData(intestazioni, dati, element, trTemplate);
			return this;
		}

		return {
			content: element,
			setData: setData
		}
	});
}


engapp.components['tabella'] = {
	create: create
};


	
})();


 