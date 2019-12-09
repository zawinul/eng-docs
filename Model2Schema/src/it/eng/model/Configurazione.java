package it.eng.model;

import java.util.ArrayList;
import java.util.List;

public class Configurazione {
	
	public String version;
	public String name;
	
	public List<Service> services = new ArrayList<Service>();
	
	public static class Service {
		public String name;
		
	}

}
