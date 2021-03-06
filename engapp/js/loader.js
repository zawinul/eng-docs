

var engapp = {
	appPages: {}, 
	status: {},
	components: {}
};

(function (){

var NOCACHE = true;
var verbose = false;

var initialLoad = [

	// bootstrap
	"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css",
	"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js",
	"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js",
	

	"img/loading.gif",
	"css/wait.css",
	"js/wait.js",

	"js/engapp-lib.js",
	"js/engapp-rest.js",
	//"js/engapp-config.js",
	"js/eng-registry-browser-client.js",

	"js/resize.js",

//	"libs/ie10-viewport-bug-workaround.css",
//	"libs/ie10-viewport-bug-workaround.js",
	"libs/bootstrap-toolkit.js",
	"js/responsive.js",
	
	"js/navigation.js",

	"css/main.css",
	
	"libs/resilient.js",
	"libs/moment.min.js",
	"libs/moment.locale.it.js",
	"libs/mousetrap.js",
	"libs/js.cookie.js",
	"libs/underscore.js",

	"oidc/client-utils.js"

];


engapp.caricaComponente = function(name) {
	return engapp.load('components/'+name+'/handler.js');
}

engapp.creaComponente = function(name) {
	var creaArgs = [];
	for(var i=1; i<arguments.length; i++)
		creaArgs.push(arguments[i]);

	return engapp.caricaComponente(name)
	.then(function() {
		if (engapp.components[name])
			return engapp.components[name].create.apply(null, creaArgs);
		else {
			engapp.load('components/' + name + '/style.css');
			var div = $('<div/>');
			var p2 = div.load('components/' + name + '/content.html');
			return p2.then(function(){
				return div.children();
			});
		}
	});
}


engapp.onStart = function(fun) {
	engapp.onStart.promise.then(fun);
}

function loaderInit() {
	// all'inizio nascondi tutto per evitare sfarfallamenti
	$('body').css({opacity:0});
	engapp.onStart.promise = loadAll().then(function(){
		return engapp.caricaComponente('layout');
	})
	.then(function(){
		engapp.components.layout.init();
	});
	engapp.onStart(function() {

		engapp.navigation.parseAddress();
		$('body').animate({opacity:1}, 500);
	});

}


$(window).resize(function(){
	
	if (window.resizeTimeout)
		clearTimeout(resizeTimeout);

	resizeTimeout=setTimeout(doIt, resizeTime);

	function doIt() {
		resized.notify();		
	}
});


function getUrlParams(url) {
	var params = {};
	url.substring(1).replace(/[?&]+([^=&]+)=([^&]*)/gi,
		function (str, key, value) { params[key] = value; });
	return params;
}

function initConfig() {
	var d = $.Deferred();
	engapp.utils.getConfig().then(
		function(c) {
			engapp.config = c;
			d.resolve();
		}
	);
	return d;
}


function loadAll() {
	return loadMultiplo(initialLoad)
		.then(initConfig)
		.then(function(){ 
			console.log(" === INITIAL LOAD COMPLETED === ");
			return true;
		});
}


var session = (new Date()).getTime();
function vurl(url) {
	if (!url.indexOf) {
		console.log(url);
	}

	// convenzione: se l'url comincia con # non occorre no-caching
	if (url.indexOf('#')==0) {
		var u = url.substring(1);
		if (u.indexOf('?')<0)
			u = u+'?';
		return u;
	}

	if (NOCACHE) {
		if (url.indexOf('_nc=')>=0)
			return url;
		if (url.indexOf('?')>=0)
			return url+'&_nc='+session;
		else
			return url+'?_nc='+session;
	}
	else if (engapp.loadVersion) {
		if (url.indexOf('?')>=0)
			return url+'&_v='+engapp.loadVersion;
		else
			return url+'?_v='+engapp.loadVersion;
	}
}

function loadHtml(container, url) {
	var v = vurl(url);
	var d = $.Deferred();
	container.load(v, function(responseText, textStatus) {
		if (textStatus == "error" ) {
			alert('errore nel caricamento di '+url);
			return;
		}
		d.resolve();
	});
	return d;
}

function loadMultiplo(arr) {
	var cur = 0;
	var d = $.Deferred();
	function f() {
		if (verbose)
			console.log('loadMultiplo '+cur+' of '+arr.length);
		if (cur>=arr.length) {
			if (verbose)
				console.log('loadMultiplo RESOLVE');
			d.resolve();
			return;
		}

		var el = arr[cur++];
		if ($.isArray(el)) { 
			//  è un array contenente un elemento jQuery su cui 
			// caricare html e l'URL
			return loadHtml(el[0], el[1]).then(f);
		}
		else
			load(el).then(f);
	}
	f();
	return d;
}

function load(url) {
	if (typeof(url)=='object' && url.jquery) { 
		// il primo parametro è un elemento jQuery su cui caricare html
		return loadHtml.apply(this, arguments);
	}

	if ($.isArray(url))
		return loadMultiplo(url);

	var v = vurl(url);
	console.log('load vurl=['+v+']');
	if (v.indexOf('.js?')>=0) 
		return loadJavascript(url);
	else if (v.indexOf('.css?')>=0)
		return loadCss(v);
	else if (v.indexOf('.png?')>=0 || v.indexOf('.jpg?')>=0 || v.indexOf('.jpeg?')>=0 || v.indexOf('.gif?')>=0)
		return loadImage(v);
	else if (v.indexOf('download')==0 && v.indexOf('type=image')>=0)
		return loadImage(v);
	else if (v.indexOf('gallery')==0)
		return loadImage(v);
	else {
		console.log('cannot load '+v);
		return $.when(1)
	}
}

var loaded = {};
function loadJavascript(url) {
	if (loaded[url]) { // i javascript si caricano una sola volta
		return loaded[url];
	}
	loaded[url] = jQuery.ajax({
		crossDomain: true,
		dataType: "script",
		url: url,
		//cache: false,
		success: function(){
			if (verbose)
				console.log('    loaded '+url);
		},
		error: function(){
			console.log('errore nel caricamento di '+url);
		}
	});	
	return loaded[url];
}

function loadCss(url, immediate) {
	console.log(`loadCss(${url}, ${immediate})`);
	if (loaded[url])  // i css si caricano una sola volta
		return loaded[url];

	if (typeof(immediate)=='undefined') 
		immediate = true; // by defaault
	
	// if (verbose);
	// 	console.log('loadCss '+url+', immediate='+immediate);

	loaded[url] = $.Deferred();
	if (immediate) {
		$('<link/>').attr('href', url).attr('rel', 'stylesheet').appendTo('head');	
		loaded[url].resolve();	
	}
	else {
		$.get(url).then(function(){
			$('<link/>').attr('href', url).attr('rel', 'stylesheet').appendTo('head');	
			loaded[url].resolve();	
		}).fail(function(){
			alert("errore nel caricamento di "+v);
		});
	}
	return loaded[url];
}

function loadImage(url, immediate) {
	if (typeof(immediate)=='undefined') 
		immediate = false; // by defaault
		
	var d = $.Deferred();
	var img = new Image();
	img.onload = function () {
		if (!immediate) d.resolve(img);
	}
	img.src = url;
	if (immediate) d.resolve(img);
	return d;
}




engapp.load = load;
$(loaderInit);
	
})()