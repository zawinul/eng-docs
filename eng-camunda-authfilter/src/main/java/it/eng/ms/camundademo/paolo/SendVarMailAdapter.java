package it.eng.ms.camundademo.paolo;

import it.eng.ms.camundademo.utils.SendMail;

//import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class SendVarMailAdapter implements JavaDelegate {
    //private static final Logger LOGGER = Logger.getAnonymousLogger();

	private static final String DEFAULT_SUBJECT = "default subject";
	private static final String DEFAULT_BODY = "default body";
	private static final String DEFAULT_TO = "paolo.andrenacci@gmail.com";
	private static final String DEFAULT_CC = "paolo.andrenacci@eng.it";
    
    public SendVarMailAdapter() {
    }



    String getStringValue(DelegateExecution scope, String name, String defaultValue){
    	Object x = scope.getVariable(name);
    	if (x==null)
    		return defaultValue;
    	else
    		return x.toString();
    }
    
	@Override
	public void execute(DelegateExecution scope) throws Exception {
		String subject = getStringValue(scope,"subject",DEFAULT_SUBJECT);
		String body = getStringValue(scope,"body",DEFAULT_BODY);
		String to = getStringValue(scope,"to",DEFAULT_TO);
		String cc = getStringValue(scope,"cc",DEFAULT_CC);
		SendMail sm = new SendMail();
		boolean ok = sm.send(subject, body, to.split(","), cc.split(","));
		if (!ok)
			throw new Exception("non sono riuscito a mandare la mail");
	}

}
