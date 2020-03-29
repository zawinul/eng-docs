package it.eng.ms.camundademo.utils;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityEngineSingleton {
	private VelocityEngine ve;
	private static VelocityEngineSingleton singleton;
	private VelocityEngineSingleton() {
		ve = new VelocityEngine();
		//ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
	}
	
	public static VelocityEngine get() {
		if (singleton==null)
			singleton = new VelocityEngineSingleton();
		return singleton.ve;
	}
}
