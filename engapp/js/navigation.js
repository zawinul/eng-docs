/*

localOpenPage
	caricaPagina
		load "<pagename>/handler.js"
			registerPage

	deactivate <currpage> (esci in caso di abort)

	jumpToTargetPage
		init (se non inizializzato)
		activatePage
			activate
			aggiorna UI (breadcrumb etc...)

*/


(function _navigation() {


var curr;
var ignoraHashChange = false;
var container;

// questa funzione viene chiamata automaticamente dal browser ad ogni cambio indirizzo
window.onhashchange = function(evt) {
	console.log("ONHASHCHANGE "+location.href);
	if (ignoraHashChange) {
		ignoraHashChange = false;
		console.log("hashchange ignorato");
	}
	else 
		parseAddress();
}


function init() {
	container = engapp.pageContainer = $('.engapp-page-container');
}


function main() {
	engapp.caricaComponente('breadcrumb').then(function(){
		engapp.components.breadcrumb.clear();
	});
	localOpenPage('home', {isMain:true});	
}

function parseAddress() {
	console.log("parseAddress: "+location.href);
	// hashAndQuery = url dal # in poi
	var hashAndQuery = location.href.split('#')[1];
	if (!hashAndQuery) 
		return main();
	

	var page = unescape(hashAndQuery.split('?')[0]);
	var query = hashAndQuery.split('?')[1];

	if (page.substring(0,1)=="/")
		page = page.substring(1);
	if (page=="")
		return main();

	console.log('next page is '+page);
	var params = {};
	query = query ? query.split("&") : [];
	for(var i=0;i<query.length; i++) {
		var par = query[i].split('=');
		var key = par[0];
		var value = par[1];
		params[unescape(key)] = unescape(value);
	}
	
	engapp.status.queryParams = params;

	// aggiorno la UI
	localOpenPage(page, params);	
}

function gotoPage(name, params) {
	console.log('goto '+name);
	engapp.components.breadcrumb.clear();
	pushPage(name, params);
}


function substPage(name, params) {
	console.log('subst '+name);
	engapp.components.breadcrumb.pop();
	pushPage(name, params);
}

// cambia l'hash sulla barra indirizzi senza causare un refresh
function changeHash(name, params) {
	var adr = name;
	params = params || {};
	var arr = [];
	for(var k in params)
		if (params[k])
			arr.push(escape(k)+"="+escape(params[k]));
	engapp.status.currHash = "";
	if (arr.length>0) {
		engapp.status.currHash = arr.join('&');
		adr += "?" + engapp.status.currHash;
	}
	curr = name;
	if (window.location.hash != '#'+adr) {
		console.log("changeHash: ignoraHashChange=true, hash="+adr+' (was '+window.location.hash+')');
		ignoraHashChange = true;
		window.location.hash = adr;
	}
}

function pushPage(name, params) {
	
	console.log('push '+name);
	var adr = "#/"+escape(name);
	params = params || {};
	var arr = [];
	for(var k in params)
		if (params[k])
			arr.push(escape(k)+"="+escape(params[k]));
	if (arr.length>0)
		adr += "?"+arr.join('&');

	curr = name;

	// JUMP!
	location.href=adr;
}

function localOpenPage(name, params) {
	var next = engapp.appPages[name];
	if (!next) {
		// la pagina 'name' non è mai stata caricata in precedenza, caricala e ripeti l'operazione
		return caricaPagina(name).then(function(){
			return localOpenPage(name, params);
		});
	}

	params = params || {};
	console.log('jumping to ' + name + ' '+!!next);
	$('body').waitStart();

	var prev = (engapp.status.currPage) 
		? engapp.appPages[engapp.status.currPage] 
		: null;

	// usciamo dalla pagina di provenienza
	if (prev) {
		var k = prev.deactivate(container, params);
		if (typeof(k)=='undefined')
			return jumpToTargetPage();
		else if (!k) 
			return abortJump(); 
		else 
			when(k).then(jumpToTargetPage, abortJump);
	}
	else
		return jumpToTargetPage(true);
	
	function abortJump() {
		$('body').waitStop();
		return Promise.reject();
	}
	
	function jumpToTargetPage() { // salta alla pagina target
		$('body').waitStop();
		engapp.status.currPage = name;
		document.title = "ENGAPP - "+name.replace('-', ' ');
		var initVal = next._initialized || next.init(container, 'pages/'+escape(name));
		var p = $.when(initVal)
			.then(function(){ 
				next._initialized=true
			})
			.then(activatePage)
			.catch(function(){
				debugger;
				alert('non è stato possibile inizializzare ['+name+']');
			});
		return p;
	}
	
	function activatePage() {
		console.log({ openPage:name, params:params});
		next.activate(container, params); 
		
		// aggiungo lo stato corrente al breadcrumb
		var breadcrumbLabel = next.getBreadcrumbLabel(params);
		return engapp.caricaComponente('breadcrumb').then(function(){
			var hash = location.href.split('#')[1];
			hash = hash ? '#'+hash: '';
			engapp.components.breadcrumb.push(breadcrumbLabel, hash);
			engapp.components.breadcrumb.draw();
		});
	}

}


function caricaPagina(name) {
	var p = engapp.load('pages/'+escape(name)+'/handler.js')
		.catch(function(){ 
			registerPage({name: name}); 
			return true;}
		);
	return p;
}

function setDefaultPageHandler(name) {

}

function susbstPage(name, params) {

}

function registerPage(cfg) {
	var base;

	var defaultConfig = {
		name: 'default',
		activate:function(){
			this.div.appendTo(container);		
		},

		deactivate:function(){
			this.div.detach();
		},

		init: defaultInit,

		getBreadcrumbLabel: function(params) {
			return this.name;
		}
	};

	cfg = $.extend(defaultConfig, cfg);
	engapp.appPages[cfg.name] = cfg; 

}

function defaultInit(container, baseUrl) {
	base = baseUrl;
	this.div = $('<div/>').appendTo(container);
	engapp.load(base+"/style.css");
	return engapp.load(this.div, base+"/content.html");
}

engapp.navigation =  {
	openPage:gotoPage,
	pushPage:pushPage,
	substPage:substPage,
	changeHash: changeHash,
	registerPage: registerPage,
	parseAddress: parseAddress,
	defaultInit: defaultInit
};

engapp.onStart(init);



})();

function buildMainPage() {
		engapp.creaComponente('menu').then(function (menu) {
			$('.engapp-menu').append(menu.content);
		});
}
