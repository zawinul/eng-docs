var fs = require('fs');
const YAML = require('yaml');
var CodeGen = require('swagger-js-codegen').CodeGen;

var swagger = JSON.parse(fs.readFileSync('eng-registry.json', 'UTF-8'));
//var swagger = YAML.parse(fs.readFileSync('myapi.yaml', 'UTF-8'));
//console.log(JSON.stringify(swagger,null,2));
var nodejsSourceCode = CodeGen.getNodeCode({ 
	className: 'EngRegistryClient',
	beautify: true, 
	swagger: swagger 
});

var angularjsSourceCode = CodeGen.getAngularCode({ className: 'Test', swagger: swagger });
//var reactjsSourceCode = CodeGen.getReactCode({ className: 'Test', swagger: swagger });
//var tsSourceCode = CodeGen.getTypescriptCode({ className: 'Test', swagger: swagger, imports: ['../../typings/tsd.d.ts'] });
fs.writeFileSync('out/nodejs.js', nodejsSourceCode);
fs.writeFileSync('out/angularjs.js', angularjsSourceCode);

