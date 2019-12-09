package it.eng.model;

import it.eng.model.SwaggerModel.ParameterDefinition;

public class Parameter {
	public String name;
	public ParameterDefinition definition;
	
	public Parameter(String name, ParameterDefinition definition) {
		this.name = name;
		this.definition = definition;
	}
	
}