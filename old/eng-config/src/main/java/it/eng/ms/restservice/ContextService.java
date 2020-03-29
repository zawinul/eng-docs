package it.eng.ms.restservice;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ContextService implements ApplicationContextAware {
    private ApplicationContext  appContext;
    private Environment env;
    
	@Override
	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		appContext = appCtx;
        env = appContext.getEnvironment();
	}

	
	public Environment getEnv() {
		return env;
	}
	
	public ApplicationContext getContext() {
		return appContext;
	}
	
	public String getProperty(String key) {
		return env.getProperty(key);
	}
}
