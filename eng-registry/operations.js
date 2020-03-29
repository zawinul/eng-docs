
const storage = require("./storage.js");

function initOperations(app) {

	app.use('/test', function(req,res){
		res.status(200).send({ msg: 'ciao!'});
	});

	app.get('/me', function (req, res) {
		res.status(200).send(
			JSON.stringify(req.userInfo, null, 2)
		);
	});

	app.get('/service/config/:serviceName', function(req, res) {
		var serviceName = req.params.serviceName;
		storage.getConfig(serviceName)
		.then(data=> {
			if (typeof(data)=='undefined')
				res.status(404).send(null);
			else
				res.status(200).send(data);	
		})
		.catch(err=>{
			res.status(500).send({error:err});
		})
	});

	app.put("/service/config/:serviceName", function(req, res) {
		var authorization = req.header.Authorization;
		// some checks...
		var serviceName = req.params.serviceName;
		storage.setConfig(serviceName, req.body);
		res.status(200).send(req.body);	
	});

	app.get("/service/info/:serviceName", function(req, res) {
		var serviceName = req.params.serviceName;
		storage.getInfo(serviceName)
		.then(data=>{
			if (typeof(data)=='undefined')
				res.status(404).send(null);
			else
				res.status(200).send(data);	
		})
		.catch(err=>{
			res.status(500).send({error:err});
		})
	});
	
	app.put("/service/info/:serviceName", function(req, res) {
		var authorization = req.header.Authorization;
		// some checks...
		var serviceName = req.params.serviceName;
		storage.setInfo(serviceName, req.body);
		res.status(200).send(req.body);	
	});


	app.get("/app/config/:appName", function(req, res) {
		var appName = req.params.appName;
		storage.getConfig(appName)
		.then(data=>{
			if (typeof(data)=='undefined')
				res.status(404).send(null);
			else
				res.status(200).send(data);	
		})
		.catch(err=>{
			res.status(500).send({error:err});
		})
	});

	app.put("/app/config/:appName", function(req, res) {

	});

	app.post("/service/endpoint/:serviceName", async function(req, res) {
		var serviceName = req.params.serviceName;
		var data = req.body;
		try {
			var ret = await storage.setEndpoint(serviceName, data.url, data.expire);
			if (typeof(ret)=='undefined')
				res.status(404).send(null);
			else
				res.status(200).send(ret);	
		}
		catch(err) {
			res.status(500).send({error:err});
		}
	});

	app.get("/service/endpoint/:serviceName", function(req, res) {
		var serviceName = req.params.serviceName;
		storage.getEndpoint(serviceName)
		.then(data=>{
			if (typeof(data)=='undefined')
				res.status(404).send(null);
			else
				res.status(200).send(data);	
		})
		.catch(err=>{
			res.status(500).send({error:err});
		})

	});

	app.get("/all", async function(req, res) {
		var data = await storage.getAll();
		res.status(200).send(data);
	});
	


}

module.exports = {
	init: initOperations
};