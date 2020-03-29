package it.eng.ms.camundademo.paolo;

import java.util.logging.Logger;

import it.eng.ms.camundademo.utils.SendMail;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class SendMailAdapter implements JavaDelegate {
    @SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	private Expression subject = new FixedValue("default subject");
	private Expression body = new FixedValue("default body");
	private Expression to = new FixedValue("paolo.andrenacci@gmail.com,paolo.andrenacci@eng.it");

    public SendMailAdapter() {
    }



	@Override
	public void execute(DelegateExecution scope) throws Exception {
		
		String s = (String) getSubject().getValue(scope);
		String b = (String) getBody().getValue(scope);
		String t = (String) getTo().getValue(scope);

    	SendMail sm = new SendMail();
    	boolean ok = sm.send(s, b, t.split(","), new String[0]);
    	if (!ok)
    		throw new Exception("Non sono riuscito a mandare la mail");
	}

	public Expression getSubject() {
		return subject;
	}

	public void setSubject(Expression subject) {
		this.subject = subject;
	}

	public Expression getBody() {
		return body;
	}

	public void setBody(Expression body) {
		this.body = body;
	}

	public Expression getTo() {
		return to;
	}

	public void setTo(Expression to) {
		this.to = to;
	}
}
