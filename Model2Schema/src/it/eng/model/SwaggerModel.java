package it.eng.model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.deser.std.MapDeserializer;

import it.eng.model.schema.Schema;


public class SwaggerModel {
	public String openapi = "3.0.1";
	public Info info;
	public Server servers[];
	public Map<String, PathDefinition> paths;
	public Components components;
	
	public SwaggerModel(Info info, Server servers[], Path paths[]) {
		this.info = info;
		this.paths = new HashMap<String, PathDefinition>();
		this.servers = servers;
		for(Path path: paths)
			this.paths.put(path.pattern, path.path);
		//this.components = components;
		
	}

	
	public static class Info {
		public String title = "a service";
		public String description = "A swagger model";
		public String version = "1.0.0";
		
		public Info(String title, String description, String version) {
			this.title = title;
			this.description = description;
			this.version = version;
		}
	}
	
	public static class Server {
		public String url;
		public Server(String url) {
			this.url = url;
		}
	}
	
	public static class PathDefinition extends HashMap<String, MethodDefinition> {	
		public PathDefinition(Method[] entries) {
			super();
			for(Method entry: entries) {
				put(entry.name, entry.definition);
			}
		}
	}
	
	
	public static class MethodDefinition {
		public String summary = "io sono un method";
		public Map<String, ParameterDefinition> parameters;
		
		public MethodDefinition(String summary, Parameter parameters[]) {
			this.summary = summary;
			if (parameters!=null && parameters.length>0) {
				this.parameters = new HashMap<String, SwaggerModel.ParameterDefinition>();
				for(Parameter p: parameters)
					this.parameters.put(p.name, p.definition);
			}
		}
	}
	
	public static class ParameterDefinition {
		public String name;
		
		public ParameterDefinition(String name) {
			this.name = name;
		}
	}
	
	public static class Components {
		public Map<String, Schema> schemas;
		
		public Components(NamedSchema schemas[]) {
			this.schemas = new HashMap<String, Schema>();
			for(NamedSchema ns: schemas) {
				this.schemas.put(ns.name, ns.schema);
			}
		}
	}
	
	public static class NamedSchema {
		public String name;
		public Schema schema;
		public NamedSchema(String name, Schema schema) {
			this.name = name;
			this.schema=schema;
		}
	}

}
