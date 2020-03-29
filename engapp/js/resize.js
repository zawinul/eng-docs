
var resized = $.Deferred();
var resizeTimeout= null, resizeTime=500;

engapp.onResize = function(fun) {
	resized.progress(fun);
}

engapp.forceResize = function(delay) {
	delay = delay || 0;
	setTimeout(function(){
		$(window).trigger(resize);
		resized.notify();
	},delay)
}
