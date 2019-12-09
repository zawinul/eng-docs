package it.eng.model.schema;

import java.util.HashMap;
import java.util.Map;

public class ObjSchema extends Schema {
	public String type = "object";
	private Map<String, Schema> properties = new HashMap<String, Schema>();
	public Map<String, Schema> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Schema> properties) {
		this.properties = properties;
	}		
}