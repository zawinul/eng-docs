package it.eng.model.lib;


import it.eng.model.Configurazione;
import it.eng.model.schema.ArraySchema;
import it.eng.model.schema.ObjSchema;
import it.eng.model.schema.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class Model2SchemaJava7 {
	
	public Schema getSchema(Type t, String genType) throws Exception {
		ObjSchema s = new ObjSchema();
		Class c = (Class) t;
		if (String.class.isAssignableFrom(c)) 
			return new Schema("string");
		if (int.class==c || double.class==c  || float.class==c || long.class==c || Integer.class==c || Double.class==c || Float.class==c || Long.class==c || BigDecimal.class==c || BigInteger.class==c) 
			return new Schema("number");
		if (boolean.class==c || Boolean.class==c)
			return new Schema("boolean");

		if (genType!=null && genType.endsWith("[]")) {
			ArraySchema ret = new ArraySchema();
			String cname = genType.substring(0, genType.length()-2);
			Class c3 = Class.forName(cname);
			Schema schild = getSchema(c3,  null);
			ret.items = schild;
			return ret;			
		}
		if (List.class.isAssignableFrom(c)) {
			ArraySchema ret = new ArraySchema();
			String cname = genType.substring(genType.indexOf("<")+1, genType.indexOf(">"));
			Class c3 = Class.forName(cname);
			Schema schild = getSchema(c3,  null);
			ret.items = schild;
			return ret;
		}
		
		for (Field f: c.getDeclaredFields()) {
			Type ft = f.getType();
			Type f2 = f.getGenericType();
			Schema child = getSchema(ft, getTypeName(ft, f2));
			s.getProperties().put(f.getName(), child);
		}
		return s;
	}

	static String getTypeName(Type t, Type gt) {
		try {
			if (t.toString().contains("[")) {
				Class ct = ((Class) t).getComponentType();
				return ct.getName()+"[]";
			}
			
			if (t instanceof GenericArrayType) {
				GenericArrayType g = (GenericArrayType) t;
				return "generic<"+g.getGenericComponentType().toString()+">";
			}
			if (gt instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) gt;
				return "<"+((Class)pt.getActualTypeArguments()[0]).getName()+">";
			}
			return ((Class)t).getName();
		}
		catch(Exception ee) {
			System.out.println(t+": error: "+ee.getMessage());
			return "???";
		}
	}
	
	public static class Esempio {
		public Double x[] = new Double[4];
		public Double y = new Double(31);
		public List<Double> z;
	}
	

	
	public static void main(String args[]) throws Exception {
		Class c = Configurazione.class;
		Schema s = new Model2SchemaJava7().getSchema(c, null);
		System.out.println(JSONUtils.toJSON(s));
		
	}
}
