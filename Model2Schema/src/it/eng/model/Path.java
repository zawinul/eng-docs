package it.eng.model;

import it.eng.model.SwaggerModel;

public class Path {
	public String pattern;
	public SwaggerModel.PathDefinition path;
	
	public Path(String pattern, Method methods[]) {
		this.pattern = pattern;
		this.path = new SwaggerModel.PathDefinition(methods);
	}
}