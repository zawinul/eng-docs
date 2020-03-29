package it.eng.ms.camundademo.r3.adapters;

import java.io.StringWriter;

import it.eng.ms.camundademo.utils.SendMail;
import it.eng.ms.camundademo.utils.VelocityEngineSingleton;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class SendMailNotifica implements JavaDelegate  {
	protected String templateName;
	
	public SendMailNotifica() {
		System.out.println("\t!!!SendMailNotifica ");
	}
	
	public SendMailNotifica(String templateName) {
		this.templateName = templateName;
		System.out.println("\t|||SendMailNotifica "+templateName);
	}
	
	@Override
	public void execute(DelegateExecution ctx) throws Exception {
		System.out.println("INVIATA NOTIFICA "+templateName);
		VelocityEngine engine = VelocityEngineSingleton.get();
		VelocityContext context = new VelocityContext();

		if (ctx!=null) {
			context.put( "businessKey", ctx.getBusinessKey());
			context.put("processId", ctx.getId());
			for (String key: ctx.getVariables().keySet()) 
				context.put( key, ctx.getVariable(key).toString());
		}
		else {
			context.put("processId", "valore process id");
			context.put("businessKey", "valore businessKey");
			context.put("mittente", "valore mittente");	
			context.put("destinatario", "valore destinatario");
			context.put("testo", "valore testo");
		}
		Template bodyTemplate = engine.getTemplate("mail-templates/"+templateName+".body.html");
		StringWriter bodyWriter = new StringWriter();
		bodyTemplate.merge(context, bodyWriter);
		
		Template subjectTemplate = engine.getTemplate("mail-templates/"+templateName+".subject.txt");
		StringWriter subjectWriter = new StringWriter();
		subjectTemplate.merge(context, subjectWriter);
		
		//String subject = (ctx==null) ?"SUBJECT": "R3 "+ctx.getBusinessKey()+": NOTIFICA "+templateName;
		String subject = subjectWriter.toString();
		String content = bodyWriter.toString();
		String to[] = new String[]{"paolo.andrenacci@gmail.com", "paolo.andrenacci@eng.it"};
		String cc[] = new String[0];
		SendMail sm = new SendMail();
		sm.send(subject, content, to, cc);
	}	
	
	
	@Bean
	public static SendMailNotifica sendInvioRichiestaConferma() {
		return new SendMailNotifica("InvioRichiestaConferma");
	}

	@Bean
	public static SendMailNotifica sendInvioNotificaRifiuto() {
		return new SendMailNotifica("InvioNotificaRifiuto");
	}
	
	@Bean
	public static SendMailNotifica sendNotificaInvioAvvenuto() {
		return new SendMailNotifica("NotificaInvioAvvenuto");
	}

	@Bean
	public static SendMailNotifica sendNotificaRicevuta() {
		return new SendMailNotifica("NotificaRicevuta");
	}

	@Bean
	public static SendMailNotifica sendNotificaTimeoutRR() {
		return new SendMailNotifica("NotificaTimeoutRR");
	}

}