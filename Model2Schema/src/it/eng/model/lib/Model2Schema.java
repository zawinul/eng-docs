package it.eng.model.lib;


import it.eng.model.schema.Schema;

import java.lang.reflect.Type;

public class Model2Schema extends Model2SchemaJava7 {
	
	public Schema getSchema(Type t, String genType) throws Exception {
		return tool.getSchema(t, genType);
	}
	
	//------------------
	private Model2SchemaJava7 tool = new Model2SchemaJava7();

}
