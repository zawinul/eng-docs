package it.eng.model;

import it.eng.model.SwaggerModel.MethodDefinition;

public class Method {
	public String name;
	public MethodDefinition definition;
	
	public Method(String methodName, MethodDefinition definition) {
		this.name = methodName;
		this.definition = definition;
	}
}