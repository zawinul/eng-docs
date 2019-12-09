package it.eng.model.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JSONUtils {
	public static String toJSON(Object x) {
		try {
			ObjectMapper mapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
				.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false)
				.configure(SerializationFeature.INDENT_OUTPUT, true);
			
			return mapper.writeValueAsString(x);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	

	public static String toYAML(Object x) {
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
				.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false)
				.configure(SerializationFeature.INDENT_OUTPUT, true);
			
			return mapper.writeValueAsString(x);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
